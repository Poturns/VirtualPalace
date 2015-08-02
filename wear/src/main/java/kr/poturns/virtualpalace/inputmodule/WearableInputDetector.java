package kr.poturns.virtualpalace.inputmodule;

import android.content.Context;

import kr.poturns.virtualpalace.input.IOperationInputFilter;
import kr.poturns.virtualpalace.input.OperationInputDetector;
import kr.poturns.virtualpalace.inputprocessor.IOutputData;
import kr.poturns.virtualpalace.inputprocessor.InputProcessor;

/**
 * Created by Myungjin Kim on 2015-07-30.
 *
 * 웨어러블에서 사용자 입력을 감지하는 InputDetector
 */
public class WearableInputDetector<Input extends IOutputData> extends OperationInputDetector<Input> implements InputProcessor.OnInputResultListener<Input> {
    protected InputProcessor<Input> mInputProcessor;

    public WearableInputDetector(Context context) {
        this(context, null, null);
    }

    public WearableInputDetector(Context context, IOperationInputFilter<Input> filter, InputProcessor<Input> inputProcessor) {
        super(context, filter);
        setInputProcessor(inputProcessor);
    }

    public void setInputProcessor(InputProcessor<Input> inputProcessor) {
        if (mInputProcessor != null)
            mInputProcessor.setResultListener(null);

        this.mInputProcessor = inputProcessor;

        if (mInputProcessor != null)
            mInputProcessor.setResultListener(this);
    }

    @Override
    public void onInputResult(Input input) {
        detect(input);
    }
}
