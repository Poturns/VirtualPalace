package kr.poturns.util;


import android.content.Context;

public interface InputHandleHelper {
    void resume();

    void pause();

    void destroy();

    void start();

    public static abstract class ContextInputHandleHelper implements InputHandleHelper {
        protected final Context context;

        protected ContextInputHandleHelper(Context context) {
            this.context = context;
        }

        public void start(){
            resume();
        }

    }
}
