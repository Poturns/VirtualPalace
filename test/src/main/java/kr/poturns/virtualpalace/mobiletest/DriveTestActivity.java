package kr.poturns.virtualpalace.mobiletest;

import android.os.Bundle;
import android.app.Activity;

import kr.poturns.virtualpalace.controller.PalaceApplication;
import kr.poturns.virtualpalace.controller.PalaceMaster;

public class DriveTestActivity extends Activity {

    PalaceApplication app;
    PalaceMaster master;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive_test);


        app = (PalaceApplication) getApplication();
        master = PalaceMaster.getInstance(app);
        master.testDrive();
    }

}
