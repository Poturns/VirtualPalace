package kr.poturns.vp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import kr.poturns.vp.media.GalleryContainerFragment;
import kr.poturns.vp.media.VideoContainerFragment;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    //private static final String TAG = "MainActivity";

    enum FragmentInfo {
        Wear(R.id.wear, WearCommunicateFragment.class.getName()),
        Speech(R.id.speech, SpeechToTextFragment.class.getName()),
        Drive(R.id.drive, DriveConnectionFragment.class.getName()),
        Gallery(R.id.gallery, GalleryContainerFragment.class.getName()),
        Video(R.id.video, VideoContainerFragment.class.getName()),;

        final int id;
        final String fname;

        FragmentInfo(int id, String fname) {
            this.id = id;
            this.fname = fname;
        }
    }

    SparseArray<Fragment> fragmentSparseArray = new SparseArray<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        for (FragmentInfo info : FragmentInfo.values())
            fragmentSparseArray.put(info.id, Fragment.instantiate(this, info.fname));

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
            for (FragmentInfo info : FragmentInfo.values())
                view.findViewById(info.id).setOnClickListener(MainActivity.this);

            return view;
        }
    };
}
