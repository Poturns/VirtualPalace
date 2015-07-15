package kr.poturns.util;

import android.app.Activity;
import android.content.Context;

public class InputHandleHelperProxy {
    InputHandleHelper[] mInputHandleHelpers = new InputHandleHelper[3];

    public static final int HELPER_WEARABLE = 0;
    public static final int HELPER_STT = 1;
    public static final int HELPER_DRIVE = 2;

    private Activity activity;

    InputHandleHelperProxy(Activity activity){
        this.activity = activity;
    }

    public void onResume() {

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (InputHandleHelper helper : mInputHandleHelpers) {
                    if (helper != null) helper.resume();
                }
            }
        });

    }

    public void onPause() {

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (InputHandleHelper helper : mInputHandleHelpers) {
                    if (helper != null) helper.pause();
                }
            }
        });

    }

    public void onDestroy() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                for (InputHandleHelper helper : mInputHandleHelpers) {
                    if (helper != null) {
                        helper.destroy();
                        mInputHandleHelpers[i++] = null;
                    }
                }

                mInputHandleHelpers = null;
            }
        });

    }

    public InputHandleHelper getInputHandleHelper(int which) {
        return mInputHandleHelpers[which];
    }

    public void createInputHandleHelperAll(){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 2; i > -1; i--) {
                    createInputHandleHelper(i);
                }
            }
        });

    }

    public InputHandleHelper createInputHandleHelper(int which) {
        InputHandleHelper helper = mInputHandleHelpers[which];
        if (helper == null) {
            helper = create(activity, which);
            mInputHandleHelpers[which] = helper;
        }

        return helper;
    }

    private InputHandleHelper create(Context context, int which) {
        switch (which) {
            case HELPER_WEARABLE:
                return new WearableCommHelper(context);
            case HELPER_STT:
                return new SpeechToTextHelper(context);
            case HELPER_DRIVE:
                return new DriveConnectionHelper(context);
            default:
                return null;
        }
    }

}
