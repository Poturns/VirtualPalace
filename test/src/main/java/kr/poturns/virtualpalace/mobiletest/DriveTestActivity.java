package kr.poturns.virtualpalace.mobiletest;

import android.app.Activity;
import android.os.Bundle;

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

        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new DriveTestFragment())
                .commit();
    }
}
