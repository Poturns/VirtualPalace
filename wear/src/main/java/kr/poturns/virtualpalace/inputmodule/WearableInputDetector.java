package kr.poturns.virtualpalace.inputmodule;

import android.content.Context;

import kr.poturns.virtualpalace.input.IOperationInputFilter;
import kr.poturns.virtualpalace.input.OperationInputDetector;
import kr.poturns.virtualpalace.inputcollector.IOutputData;
import kr.poturns.virtualpalace.inputcollector.InputCollector;

/**
 * Created by Myungjin Kim on 2015-07-30.
 *
 * 웨어러블에서 사용자 입력을 감지하는 InputDetector
 */
public class WearableInputDetector<Input extends IOutputData> extends OperationInputDetector<Input> implements InputCollector.OnInputResultListener<Input> {
    protected InputCollector<Input> mInputCollector;

    public WearableInputDetector(Context context) {
        this(context, null, null);
    }

    public WearableInputDetector(Context context, IOperationInputFilter<Input> filter, InputCollector<Input> inputCollector) {
        super(context, filter);
        setInputProcessor(inputCollector);
    }

    public void setInputProcessor(InputCollector<Input> inputCollector) {
        if (mInputCollector != null)
            mInputCollector.setResultListener(null);

        this.mInputCollector = inputCollector;

        if (inputCollector != null)
            inputCollector.setResultListener(this);
    }

    @Override
    public void onInputResult(Input input) {
        detect(input);
    }
}
