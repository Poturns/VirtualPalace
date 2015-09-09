package kr.poturns.virtualpalace;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;

import java.io.IOException;

import kr.poturns.virtualpalace.communication.WearMessageObject;
import kr.poturns.virtualpalace.inputmodule.wear.WearInputConnector;

/**
 * Wearable 연결 테스트 Fragment
 * Created by Myungjin Kim on 2015-09-03.
 */
public class WearCommunicateFragment extends Fragment implements MessageApi.MessageListener {

    private WearInputConnector wearInputConnector;

    private TextView textView;
    private StringBuilder sb = new StringBuilder();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wearInputConnector = new WearInputConnector(getActivity());
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
        wearInputConnector.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        wearInputConnector.disconnect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        wearInputConnector.destroy();
    }

    void printText() {
        textView.setText(sb.toString());
    }

    @Override
    public void onMessageReceived(final MessageEvent messageEvent) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final WearMessageObject messageObject = WearMessageObject.fromBytes(messageEvent.getData());
                    if (messageObject.dataSet != null && messageObject.dataSet.length > 0) {
                        sb.append(messageObject);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                printText();
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("WearFragment", "주어진 바이트 배열로 부터 메시지를 생성할 수 없습니다.");
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    Log.e("WearFragment", "WearMessageObject 클래스 정보를 찾을 수 없습니다.");
                }
            }
        });
    }
}
