package kr.porturns.vp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    //private static final String TAG = "MainActivity";

    SparseArray<Fragment> fragmentSparseArray = new SparseArray<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentSparseArray.put(R.id.wear, new WearCommunicateFragment());
        fragmentSparseArray.put(R.id.speech, new SpeechToTextFragment());
        fragmentSparseArray.put(R.id.drive, new DriveConnectionFragment());

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, mainFragment)
                .commit();

    }

    @Override
    public void onClick(View v) {

        Fragment f = fragmentSparseArray.get(v.getId());

        if (f != null)
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, f)
                    .addToBackStack(null)
                    .commit();

    }

    private Fragment mainFragment = new Fragment() {
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_main, container, false);
            final int[] ids = {R.id.wear, R.id.speech, R.id.drive};
            for (int id : ids)
                view.findViewById(id).setOnClickListener(MainActivity.this);

            return view;
        }
    };
}
