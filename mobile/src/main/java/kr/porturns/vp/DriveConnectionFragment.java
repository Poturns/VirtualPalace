package kr.porturns.vp;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import kr.poturns.util.DriveConnectionHelper;

public class DriveConnectionFragment extends Fragment {
    private DriveConnectionHelper mDriveConnectionHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDriveConnectionHelper = new DriveConnectionHelper(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        mDriveConnectionHelper.connect();
    }

    @Override
    public void onPause() {
        super.onPause();

        mDriveConnectionHelper.disconnect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mDriveConnectionHelper.release();
    }
}
