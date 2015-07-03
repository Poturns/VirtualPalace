package kr.porturns.vp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    //private static final String TAG = "MainActivity";

    private Fragment wear = new WearCommunicateFragment(), speech = new SpeechToTextFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, mainFragment)
                .commit();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.wear:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, wear)
                        .addToBackStack(null)
                        .commit();
                break;

            case R.id.speech:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, speech)
                        .addToBackStack(null)
                        .commit();
                break;
        }
    }

    private Fragment mainFragment = new Fragment() {
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_main, container, false);
            view.findViewById(R.id.wear).setOnClickListener(MainActivity.this);
            view.findViewById(R.id.speech).setOnClickListener(MainActivity.this);
            return view;
        }
    };
}
