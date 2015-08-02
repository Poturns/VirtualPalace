package kr.poturns.virtualpalace.inputprocessor;

/**
 * Created by Myungjin Kim on 2015-07-30.
 * <p/>
 * 입력을 받아 적절한 데이터로 변환하여 알려주는 클래스
 */
public interface InputProcessor<Output extends IOutputData> {

    void setResultListener(OnInputResultListener<Output> listener);

    interface OnInputResultListener<Output> {
        void onInputResult(Output output);
    }

    /**
     * 입력 변환을 시작한다.
     */
    void startListening();

    /**
     * 입력 변환을 종료한다.
     */
    void stopListening();

    abstract class Base<T extends IOutputData> implements InputProcessor<T> {
        protected OnInputResultListener<T> listener;

        @Override
        public void setResultListener(OnInputResultListener<T> listener) {
            this.listener = listener;
        }

    }
}
