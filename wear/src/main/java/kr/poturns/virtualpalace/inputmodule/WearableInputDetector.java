package kr.poturns.virtualpalace.inputmodule;

import kr.poturns.virtualpalace.input.IOperationInputFilter;
import kr.poturns.virtualpalace.input.OperationInputDetector;
import kr.poturns.virtualpalace.inputcollector.InputCollector;

/**
 * Created by Myungjin Kim on 2015-07-30.
 * <p/>
 * 웨어러블에서 사용자 입력을 감지하는 InputDetector
 */
public class WearableInputDetector<Input> extends OperationInputDetector<Input> implements InputCollector.OnInputResultListener<Input> {
    protected InputCollector<Input> mInputCollector;

    public WearableInputDetector(IOperationInputFilter<Input> filter, InputCollector<Input> inputCollector) {
        super(filter);
        setInputCollector(inputCollector);
    }

    /**
     * 입력을 수집/처리할 InputCollector 객체를 설정한다.
     */
    public void setInputCollector(InputCollector<Input> inputCollector) {
        if (mInputCollector != null)
            mInputCollector.setResultListener(null);

        this.mInputCollector = inputCollector;

        if (inputCollector != null)
            inputCollector.setResultListener(this);
    }

    @Override
    public final void onInputResult(Input input) {
        detect(input);
    }
}
