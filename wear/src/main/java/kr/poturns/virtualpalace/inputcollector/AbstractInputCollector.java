package kr.poturns.virtualpalace.inputcollector;

/**
 * InputCollector의 대략적인 구현
 * Created by Myungjin Kim on 2015-09-02.
 */
public abstract class AbstractInputCollector<T> implements InputCollector<T> {
    protected OnInputResultListener<T> listener;

    @Override
    public void setResultListener(OnInputResultListener<T> listener) {
        this.listener = listener;
    }

}
