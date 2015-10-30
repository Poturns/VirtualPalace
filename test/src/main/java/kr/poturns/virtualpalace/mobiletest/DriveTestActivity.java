package kr.poturns.virtualpalace.mobiletest;

<<<<<<< HEAD
import android.os.Bundle;
import android.app.Activity;
import android.os.Bundle;

import kr.poturns.virtualpalace.controller.PalaceApplication;
import kr.poturns.virtualpalace.controller.PalaceMaster;

public class DriveTestActivity extends Activity {

    PalaceApplication app;
    PalaceMaster master;

/**
 * Created by Myungjin Kim on 2015-10-30.
 *
 * DriveAssistant test Activity
 */
public class DriveTestActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive_test);


        app = (PalaceApplication) getApplication();
        master = PalaceMaster.getInstance(app);
        master.testDrive();
    }

        setContentView(R.layout.activity_drive_test);

        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new DriveRestTestFragment())
                .commit();
    }
}
