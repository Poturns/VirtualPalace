package kr.poturns.virtualpalace.augmented;

public class mKalmanFilter {
//	private final KalmanFilter mFilterF;
//
//	private int pLen;
//	private int mLen;
//
	private double[] lastMeasurement;
	private double[] lastPredict;
	private double[] lastEstimated;

	public mKalmanFilter(double[] initData) {
//		this.mLen = initData.length;
//		this.pLen = initData.length*2;
//		mFilterF = new KalmanFilter(pLen, mLen, 0, CvType.CV_32F);
//
//		lastMeasurement = new double[mLen];
//		lastPredict = new double[pLen];
//		lastEstimated = new double[mLen];
//
//		// 전환행렬(p*p): x'+=x+v, v'+=v
//		Mat transitionMat = new Mat(pLen, pLen, CvType.CV_32F, new Scalar(0));
//		float[] tM_val = new float[pLen*pLen];
//		for(int i=0;i<pLen;i++) {
//			tM_val[i*pLen+i] = 1;
//			if(i<mLen)
//				tM_val[i*pLen+i+mLen] = 1;
//		}
//		transitionMat.put(0, 0, tM_val);
//		mFilterF.set_transitionMatrix(transitionMat);
//
//		//초기값(m*1): input
//		Mat measurement = toMat(initData);
//		mFilterF.set_statePre(measurement);
//
//		//측정행렬(m*p): p-square diagonal 1
//		Mat measurementMat = Mat.eye(mLen, pLen, CvType.CV_32F);
//		mFilterF.set_measurementMatrix(measurementMat);
//
//		//프로세스 잡음 공분산(p*p): p-square diagonal 10^-5
//		Mat pNoiseCov = Mat.eye(pLen, pLen, CvType.CV_32F);
//		pNoiseCov = pNoiseCov.mul(pNoiseCov, 1e-5);
//		mFilterF.set_processNoiseCov(pNoiseCov);
//
//		//측정 잡음 공분산(m*m): m-square diagonal 10^-1
//		Mat mNoiseCov = Mat.eye(mLen, mLen, CvType.CV_32F);
//		mNoiseCov = mNoiseCov.mul(mNoiseCov, 1e-1);
//		mFilterF.set_measurementNoiseCov(mNoiseCov);
//
//		//차후오차 공분산(p*p): p-square diagonal 1
//		Mat postErrorCov = Mat.eye(pLen, pLen, CvType.CV_32F);
//		mFilterF.set_errorCovPost(postErrorCov);
	}

	public double[] predict() {
//		Mat prediction = mFilterF.predict();
//		for(int i=0;i<pLen;i++) {
//			lastPredict[i] = prediction.get(i,0)[0];
//		}
		return lastPredict;
	}

	public double[] update(double[] data) {
//		Mat measurement = toMat(data);
//		Mat estimated = mFilterF.correct(measurement);
//		for(int i=0;i<mLen;i++) {
//			lastEstimated[i] = estimated.get(i, 0)[0];
//		}
		return lastEstimated;
	}

	public double[] getLatestMeasurement(){return lastMeasurement;}
	public double[] getLatestPrediction(){return lastPredict;}
	public double[] getLatestEstimation(){return lastEstimated;}

//	private Mat toMat(double[] data) {
//		Mat mat = new Mat(mLen, 1, CvType.CV_32F, new Scalar(0));
//		for(int i=0;i<mLen;i++) {
//			mat.put(i, 0, data[i]);
//		}
//		return mat;
//	}
}
