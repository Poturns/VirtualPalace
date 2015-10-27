#include <opencv2/calib3d.hpp>
#include <opencv2/core/core.h>
#include <opencv2/core/core_c.h>
#include <opencv2/core/cvstd.hpp>
#include <opencv2/core/mat.hpp>
#include <opencv2/core/mat.inl.hpp>
#include <opencv2/core/types.hpp>
#include <opencv2/core/utility.hpp>
#include <opencv2/core.hpp>
#include <opencv2/features2d.hpp>
#include <opencv2/hal/defs.h>
#include <opencv2/imgproc/imgproc.h>
#include <opencv2/imgproc/imgproc_c.h>
#include <opencv2/imgproc.hpp>
#include <opencv2/video/tracking.hpp>

#define ORB_CORNER_SIZE			2000
#define ORB_PYR_LEVEL				4
#define ORB_FAST_THRESHOLD		12

#define EKF_THRESHOLD				12.0f
#define KNN_THRESHOLD			2.0f

#define LOG_TAG "kr_poturns@shell"
#define LOG_I(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

using namespace std;
using namespace cv;

string fromInt(int num) {
	stringstream ss;
	ss << num;
	return ss.str();
}


//Shi-Tomashi feature detecting parameters
#define ORB_CORNER_SIZE			2000
#define ORB_PYR_LEVEL				4
#define ORB_FAST_THRESHOLD		12

#define ST_MAX_CORNER		1500
#define ST_QUALITY_LV		0.01
#define ST_MIN_DIST			3
#define ST_BOX_SIZE			9
#define ST_THRESHOLD		0.04

#define OPTICALFLOW_THRESHOLD	12.0f
#define KNN_THRESHOLD				2.0f

class Frame {
public:
	int64 timestamp;
	Mat gray;
	vector<Point2f> pts;
	Mat desc;
	Mat_<double> rotation;
	Mat_<double> transpose;

	Frame(Mat& img) {
		cvtColor(img, gray, COLOR_BGR2GRAY);
		vector<KeyPoint> kpts;
		Ptr<ORB> orb = ORB::create(ORB_CORNER_SIZE, 2, ORB_PYR_LEVEL, 11, 0, 2, ORB::HARRIS_SCORE, ORB_FAST_THRESHOLD);
		orb->detect(gray, kpts);
		orb->compute(gray, kpts, desc);
		for(int i=0;i<kpts.size();i++) {
			KeyPoint kp = kpts[i];
			pts.push_back(kp.pt);
		}

		timestamp = getTickCount();
	}

	void release() {
		gray.release();
		desc.release();
		rotation.release();
		transpose.release();
	}

	void draw(Mat& img) {
		for(unsigned int i=0;i<pts.size();i++) {
			Point2f pt = pts[i];
			circle(img, pt, 3, Scalar(255, 0, 0), 2, 8, 0);
		}
	}
};


class Match {
public:
	Frame* pPrev;
	Frame* pNext;
	vector<DMatch> matches;
	vector<Point2f> goodpts_prev;
	vector<Point2f> goodpts_next;
	Mat FMat;
	Mat EMat;

	Match(Frame& f1, Frame& f2) {
		pPrev = &f1;
		pNext = &f2;
		int sz = f1.pts.size();
		vector<Point2f> cnr1;
		vector<Point2f> cnr2 = f2.pts;
		vector<int> cnr1_idx;
		vector<uchar> status(sz);
		vector<float> error(sz);
		vector<Point2f> tmp(sz);
		vector<vector<DMatch> > matches_list;
		calcOpticalFlowPyrLK(f1.gray, f2.gray, f1.pts, tmp, status, error);
		for(unsigned int i=0;i<status.size();i++) {
			if(status[i]==1 && error[i] < EKF_THRESHOLD) {
				Point2f pt = tmp[i];
				cnr1_idx.push_back(i);
				cnr1.push_back(pt);
			}
		}
		int f1sz = f1.pts.size();
		int f2sz = f2.pts.size();
		LOG_I("f1: %d, f2: %d", f1sz, f2sz);
		Mat desc1 = Mat(cnr1).reshape(1, f1sz);
		Mat desc2 = Mat(cnr2).reshape(1, f2sz);
		BFMatcher matcher(CV_L2);
		matcher.radiusMatch(desc1, desc2, matches_list, KNN_THRESHOLD);

		for(unsigned int i=0;i<matches_list.size();i++) {
			vector<DMatch>& vm = matches_list[i];
			if(vm.size()==1) {
				DMatch& m = vm[0];
				m.queryIdx = cnr1_idx[m.queryIdx];
				matches.push_back(m);
				Point2f gpts_prev = pPrev->pts[m.queryIdx];
				Point2f gpts_next = pNext->pts[m.trainIdx];
				goodpts_prev.push_back(gpts_prev);
				goodpts_next.push_back(gpts_next);
			} else if(vm.size()>1){
				DMatch& m0 = vm[0];
				DMatch& m1 = vm[1];
				if(m0.distance / m1.distance < 0.7) {
					DMatch& m = matches_list[i][0];
					m.queryIdx = cnr1_idx[m.queryIdx];
					matches.push_back(m);
					Point2f gpts_prev = pPrev->pts[m.queryIdx];
					Point2f gpts_next = pNext->pts[m.trainIdx];
					goodpts_prev.push_back(gpts_prev);
					goodpts_next.push_back(gpts_next);
				} else {
					continue;
				}
			}
		}
	}

	void release() {

	}

	void draw(Mat& img) {
		for(unsigned int i=0;i<matches.size();i++) {
			DMatch m = matches[i];
			Point2f prev = pPrev->pts[m.queryIdx];
			Point2f next = pNext->pts[m.trainIdx];
			line(img, prev, next, Scalar(255, 0, 0), 2);
			circle(img, next, 5, Scalar(255, 0, 0), 2, CV_FILLED, 0);
		}
	}

	Mat getFundamentalMatrix(vector<Point2f>& predpts1, vector<Point2f>& predpts2, vector<DMatch>& predmatches) {
		predpts1.clear();
		predpts2.clear();
		predmatches.clear();
		vector<uchar> status;
		Mat F;
		double min, max;
		minMaxIdx(goodpts_prev, &min, &max);
		F = findFundamentalMat(goodpts_prev, goodpts_next, FM_RANSAC, 0.006*max, 0.99, status);
		for(unsigned int i=0;i<status.size();i++) {
			if(status[i]==1) {
				predpts1.push_back(goodpts_prev[i]);
				predpts2.push_back(goodpts_next[i]);
				predmatches.push_back(matches[i]);
			}
		}
	}

	double prune() {
		vector<Point2f> cnr1, cnr2;
		vector<DMatch> nmatches;
		int before_sz = matches.size();
		getFundamentalMatrix(cnr1, cnr2, nmatches);
		goodpts_prev = cnr1;
		goodpts_next = cnr2;
		matches = nmatches;
		FMat = getFundamentalMatrix(cnr1, cnr2, nmatches);

		return (double)nmatches.size()/before_sz;
	}
};

#define ARMAP_MAX_TRAIL_SIZE	10

class ARMap {
public:
	vector<Frame> trail;
	vector<Match> matches;

	void init() {
		while(trail.size()>0) {
			trail.begin()->release();
			trail.erase(trail.begin());
		}
	}

	int64 addFrame(Mat& img) {
		Frame f(img);
		trail.push_back(f);
		if(trail.size()>ARMAP_MAX_TRAIL_SIZE) {
			trail.begin()->release();
			trail.erase(trail.begin());
			matches.erase(matches.begin());
			LOG_I("Frame has been deleted");
		}

		if(trail.size()>10) {
			Frame& f1 = trail[trail.size()-2];
			Frame& f2 = trail[trail.size()-1];
			Match match(f1, f2);
			matches.push_back(match);
		}
		return f.timestamp;
	}

	int draw(Mat& img) {
		if(matches.size()<=0)
			return -1;
		Frame& f = trail[trail.size()-1];
		f.draw(img);
		Match& m = matches[matches.size()-1];
		m.draw(img);
		return (int)m.matches.size();
	}

	void prune() {

	}
};

extern "C"{

ARMap g_armap;

JNIEXPORT void JNICALL Java_kr_poturns_virtualpalace_ar_MapMaker_init(JNIEnv*, jobject);
JNIEXPORT jlong JNICALL Java_kr_poturns_virtualpalace_ar_MapMaker_add(JNIEnv*, jobject, jlong addr_img);
JNIEXPORT jint JNICALL Java_kr_poturns_virtualpalace_ar_MapMaker_draw(JNIEnv*, jobject, jlong addr_img);
JNIEXPORT jint JNICALL Java_kr_poturns_virtualpalace_ar_MapMaker_gettrailsize(JNIEnv*, jobject);



JNIEXPORT void JNICALL Java_kr_poturns_virtualpalace_ar_MapMaker_init(JNIEnv*, jobject) {
	g_armap.init();
}

JNIEXPORT jlong JNICALL Java_kr_poturns_virtualpalace_ar_MapMaker_add(JNIEnv*, jobject, jlong addr_img) {
	if(addr_img==0){
		LOG_I("addr_img is NULL");
		return 0;
	}
	Mat& img = *(Mat*)addr_img;
	jlong stamp = g_armap.addFrame(img);
	return stamp;
}

JNIEXPORT jint JNICALL Java_kr_poturns_virtualpalace_ar_MapMaker_draw(JNIEnv*, jobject, jlong addr_img) {
	if(addr_img==0)
		return -1;
	return -1;
	Mat& img = *(Mat*)addr_img;
	jint size = g_armap.draw(img);
	return size;
}

JNIEXPORT jint JNICALL Java_kr_poturns_virtualpalace_ar_MapMaker_gettrailsize(JNIEnv*, jobject) {
	return g_armap.trail.size();
}
}

