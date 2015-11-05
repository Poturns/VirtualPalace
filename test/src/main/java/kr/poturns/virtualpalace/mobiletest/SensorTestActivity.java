package kr.poturns.virtualpalace.mobiletest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import kr.poturns.virtualpalace.InfraDataService;
import kr.poturns.virtualpalace.controller.PalaceApplication;
import kr.poturns.virtualpalace.sensor.ISensorAgent;


public class SensorTestActivity extends Activity {

    Intent mInfraServiceIntent;

    TextView accel, gyro, orientation, location, network, battery;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_test);


        mInfraServiceIntent = new Intent(this, InfraDataService.class);
        startService(mInfraServiceIntent);

        accel = (TextView) findViewById(R.id.accelero);
        gyro = (TextView) findViewById(R.id.gyro);
        orientation = (TextView) findViewById(R.id.orientation);
        location = (TextView) findViewById(R.id.location);
        network = (TextView) findViewById(R.id.network);
        battery = (TextView) findViewById(R.id.battery);
    }

    Timer timer;

    @Override
    protected void onResume() {
        super.onResume();

        InfraDataService service = ((PalaceApplication) getApplication()).getInfraDataService();
        if (service != null)
            service.startListeningActivated();

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        InfraDataService service = ((PalaceApplication) getApplication()).getInfraDataService();
                        if (service == null) {
                            return;
                        }

                        double[] data;

                        data = service.getSensorAgent(ISensorAgent.TYPE_AGENT_ACCELEROMETER).getLatestData();
                        accel.setText( "X : " + String.format("%.5f", data[1]) + "\nY : " + String.format("%.5f", data[2]) + "\nZ : " + String.format("%.5f", data[3]));

                        data = service.getSensorAgent(ISensorAgent.TYPE_AGENT_GYROSCOPE).getLatestData();
                        gyro.setText( "X : " + String.format("%.5f", data[1]) + "\nY : " + String.format("%.5f", data[2]) + "\nZ : " + String.format("%.5f", data[3]));

                        //orientation.setText(service.getSensorAgent(ISensorAgent.TYPE_AGENT_ACCELEROMETER).getLatestData().toString());

                        data = service.getSensorAgent(ISensorAgent.TYPE_AGENT_LOCATION).getLatestData();
                        location.setText("LAT : " + data[1] + "\nLNG : " + data[2] + "\nALT : " + data[3]);

                        data = service.getSensorAgent(ISensorAgent.TYPE_AGENT_NETWORK).getLatestData();
                        network.setText("");

                        data = service.getSensorAgent(ISensorAgent.TYPE_AGENT_BATTERY).getLatestData();
                        battery.setText("EXPANSION : " + data[1] + "\nLEVEL : " + data[2] + "\nTEMPERATURE : " + data[3]);
                    }
                });
            }
        }, 1000, 100);

    }

    @Override
    protected void onPause() {
        timer.cancel();

        InfraDataService service = ((PalaceApplication) getApplication()).getInfraDataService();
        if (service != null)
            service.stopListeningActivated();

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        stopService(mInfraServiceIntent);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}
