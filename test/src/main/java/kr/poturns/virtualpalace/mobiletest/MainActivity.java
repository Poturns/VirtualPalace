package kr.poturns.virtualpalace.mobiletest;

import android.os.Bundle;

import org.opencv.android.CameraBridgeViewBase;

import kr.poturns.virtualpalace.ArgumentActivity;

public class MainActivity extends ArgumentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected CameraBridgeViewBase getOpenCvCameraView() {
        return (CameraBridgeViewBase) findViewById(R.id.camera_view);
    }
}
