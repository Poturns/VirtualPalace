package kr.poturns.virtualpalace.inputcollector;

import android.content.Context;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import kr.poturns.virtualpalace.inputmodule.GestureInputFilter;
import kr.poturns.virtualpalace.unity.R;

/**
 * 제스쳐 입력을 수집하여 적절히 걸러내는 InputCollector
 * <p/>
 * Created by Myungjin Kim on 2015-09-03.
 */
public class GestureInputCollector extends AbstractInputCollector<String> implements GestureOverlayView.OnGesturePerformedListener, View.OnClickListener {
    private final GestureLibrary gestureLibrary;
    private boolean isListening = false;
    private OnInputResultListener<String> mAdditionalListener;

    public GestureInputCollector(Context context) {
        gestureLibrary = GestureLibraries.fromRawResource(context, R.raw.gestures);

        if (!gestureLibrary.load()) {
            Log.w("Gesture", "could not load gesture library");
            throw new RuntimeException("could not load gesture library");
        }
    }

    public void setAdditionalListener(OnInputResultListener<String> listener){
        this.mAdditionalListener = listener;
    }

    @Override
    public void startListening() {
        isListening = true;
    }

    @Override
    public void stopListening() {
        isListening = false;
    }

    @Override
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
        if (listener == null || !isListening)
            return;

        ArrayList<Prediction> predictions = gestureLibrary.recognize(gesture);

        if (predictions.size() > 0) {
            Prediction prediction = predictions.get(0);
            if (prediction.score > 6.0) {
                listener.onInputResult(prediction.name);
                if(mAdditionalListener != null)
                    mAdditionalListener.onInputResult(prediction.name);
            }
        }

    }

    @Override
    public void onClick(View v) {
        if (listener == null || !isListening)
            return;

        listener.onInputResult(GestureInputFilter.OPERATION_CLICK);
    }
}
