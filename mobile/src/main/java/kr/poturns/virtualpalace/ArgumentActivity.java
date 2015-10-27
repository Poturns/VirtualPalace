package kr.poturns.virtualpalace;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import kr.poturns.virtualpalace.ar.MapMaker;
import kr.poturns.virtualpalace.controller.PalaceApplication;

public abstract class ArgumentActivity extends Activity implements CvCameraViewListener2, View.OnTouchListener {
    //private static String LOG_TAG = "ArgumentActivity";
    private CameraBridgeViewBase mOpenCvCameraView;
    private MapMaker mapmaker;
    Intent mInfraServiceIntent;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    mOpenCvCameraView.setOnTouchListener(ArgumentActivity.this);
                    mOpenCvCameraView.enableView();
                    mapmaker = new MapMaker();
                    mapmaker.start();
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mInfraServiceIntent = new Intent(this, InfraDataService.class);
        startService(mInfraServiceIntent);

        mOpenCvCameraView = getOpenCvCameraView();
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCameraIndex(0);
        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    protected abstract CameraBridgeViewBase getOpenCvCameraView();

    @Override
    protected void onResume() {
        super.onResume();
        InfraDataService service = ((PalaceApplication) getApplication()).getInfraDataService();
        if (service != null)
            service.startListening();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
    }

    @Override
    protected void onPause() {
        super.onPause();

        InfraDataService service = ((PalaceApplication) getApplication()).getInfraDataService();
        if (service != null)
            service.stopListening();

        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
        if (mapmaker != null)
            mapmaker.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }


    @Override
    public void onCameraViewStarted(int width, int height) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onCameraViewStopped() {
        // TODO Auto-generated method stub

    }

    @Override
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        Mat rgba = inputFrame.rgba();
        mapmaker.setImg(rgba);
        return rgba;
    }

    @Override
    public boolean onTouch(View view, MotionEvent e) {
        return false;
    }
}
