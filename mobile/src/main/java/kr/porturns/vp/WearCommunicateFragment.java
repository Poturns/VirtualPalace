package kr.porturns.vp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;

import kr.poturns.util.WearableCommHelper;

public class WearCommunicateFragment extends Fragment implements WearableCommHelper.MessageListener {

    private WearableCommHelper wearableCommHelper;

    private TextView textView;
    private StringBuilder sb = new StringBuilder();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wearableCommHelper = new WearableCommHelper(getActivity(), new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle bundle) {
                sb.append("google api client connected.").append('\n');
                sb.append(bundle).append("\n\n");
                printText();
            }

            @Override
            public void onConnectionSuspended(int i) {
                sb.append("\ngoogle api client suspended.\n\n");
                printText();
            }
        });
        wearableCommHelper.setMessageListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_wear, container, false);
        textView = (TextView) view.findViewById(android.R.id.text1);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        wearableCommHelper.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        wearableCommHelper.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        wearableCommHelper.destroy();
    }

    void printText() {
        textView.setText(sb.toString());
    }

    @Override
    public void onMessageReceived(String path, Object data) {
        switch (path) {
            case WearableCommHelper.DATA_TRANSFER_MESSAGE_PATH: {
                //MovementData movementData = (MovementData) data;
                //String s = data.toString();
                //sb.append("Movement Data : \n").append(s).append('\n');
                sb.append("Movement Data : \n").append(data).append('\n');
                break;
            }

            case WearableCommHelper.SEND_STRING_MESSAGE_PATH: {
                //String s = (String) data;
                //sb.append(s).append('\n');
                sb.append(data).append('\n');
                break;
            }

            default:
                return;
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                printText();
            }
        });
    }
}
