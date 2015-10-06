package kr.poturns.virtualpalace;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import kr.poturns.virtualpalace.inputcollector.GestureInputCollector;
import kr.poturns.virtualpalace.inputcollector.SensorInputCollector;
import kr.poturns.virtualpalace.inputmodule.WearInputConnector;

/**
 * WearableMainActivity 에 attach 되는 Fragment
 * Created by Myungjin Kim on 2015-09-02.
 */
public abstract class BaseFragment extends Fragment {
    private WearableMainActivity wearableMainActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActivity() instanceof WearableMainActivity) {
            wearableMainActivity = (WearableMainActivity) getActivity();
        } else {
            Toast.makeText(getActivity(), "invalid activity!!", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }
    }

    @Nullable
    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getLayoutResId(), container, false);
    }

    /**
     * Fragment의 레이아웃 xml의 id를 반환한다.
     */
    @LayoutRes
    protected abstract int getLayoutResId();

    /**
     * SensorInputCollector 를 얻는다.
     */
    public final SensorInputCollector getSensorInputCollector() {
        return wearableMainActivity.getSensorInputCollector();
    }

    /**
     * GestureInputCollector 를 얻는다.
     */
    public final GestureInputCollector getGestureInputCollector() {
        return wearableMainActivity.getGestureInputCollector();
    }

    /**
     * WearInputConnector 를 얻는다.
     */
    public final WearInputConnector getWearInputConnector(){
        return wearableMainActivity.getWearInputConnector();
    }

}
