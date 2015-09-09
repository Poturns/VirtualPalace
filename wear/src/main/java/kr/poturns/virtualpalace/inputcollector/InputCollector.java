package kr.poturns.virtualpalace.inputcollector;

/**
 * Created by Myungjin Kim on 2015-07-30.
 * <p/>
 * 입력을 수집하여 적절한 데이터로 변환하여 알려주는 클래스
 */
public interface InputCollector<Output> {

    /**
     * 변환된 입력 데이터를 전달받을 리스너를 등록한다.
     *
     * @param listener 변환된 입력 데이터를 전달받을 리스너
     */
    void setResultListener(OnInputResultListener<Output> listener);

    /**
     * 변환된 입력 데이터를 전달받는 리스너
     */
    interface OnInputResultListener<Output> {
        /**
         * 변환된 입력 데이터를 전달받는다.
         *
         * @param output 변환된 입력 데이터
         */
        void onInputResult(Output output);
    }

    /**
     * 입력 수집을 시작한다.
     */
    void startListening();

    /**
     * 입력 수집을 종료한다.
     */
    void stopListening();

}
