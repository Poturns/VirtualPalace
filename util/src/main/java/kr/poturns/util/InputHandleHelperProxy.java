package kr.poturns.util;

import android.content.Context;

public class InputHandleHelperProxy {
    InputHandleHelper[] mInputHandleHelpers = new InputHandleHelper[3];

    public static final int HELPER_WEARABLE = 0;
    public static final int HELPER_STT = 1;
    public static final int HELPER_DRIVE = 2;

    public void onResume() {

        for (InputHandleHelper helper : mInputHandleHelpers) {
            if (helper != null) helper.resume();
        }

    }

    public void onPause() {

        for (InputHandleHelper helper : mInputHandleHelpers) {
            if (helper != null) helper.pause();
        }

    }

    public void onDestroy() {
        int i = 0;
        for (InputHandleHelper helper : mInputHandleHelpers) {
            if (helper != null) {
                helper.destroy();
                mInputHandleHelpers[i++] = null;
            }
        }

        mInputHandleHelpers = null;
    }

    public InputHandleHelper getInputHandleHelper(int which) {
        return mInputHandleHelpers[which];
    }

    public void startInputHandleHelper(InputHandleHelper inputHandleHelper) {
        inputHandleHelper.resume();
    }

    public void stopInputHandleHelper(InputHandleHelper inputHandleHelper) {
        inputHandleHelper.pause();
    }

    public InputHandleHelper createInputHandleHelper(Context context, int which) {
        InputHandleHelper helper = mInputHandleHelpers[which];
        if (helper == null) {
            helper = create(context, which);
            mInputHandleHelpers[which] = helper;
        }

        return helper;
    }

    private static InputHandleHelper create(Context context, int which) {
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
