package kr.poturns.virtualpalace.mobiletest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.drive.Drive;

public class MainActivity extends Activity implements View.OnClickListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_sensortest).setOnClickListener(this);
        findViewById(R.id.btn_dbtest).setOnClickListener(this);
        findViewById(R.id.btn_controllertest).setOnClickListener(this);
        findViewById(R.id.btn_drivetest).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
      switch(view.getId()) {
          case R.id.btn_sensortest:
              intent = new Intent(this, SensorTestActivity.class);
              break;

          case R.id.btn_dbtest:
              intent = new Intent(this, DatabaseTestActivity.class);
              break;

          case R.id.btn_controllertest:
              intent = new Intent(this, ControllerTestActivity.class);
              break;

          case R.id.btn_drivetest:
              intent = new Intent(this, DriveTestActivity.class);
              break;
      }

        if (intent != null)
            startActivity(intent);
    }
}
