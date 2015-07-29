package kr.poturns.virtualpalace.inputmodule;

import android.content.Context;

import kr.poturns.virtualpalace.input.IOperationInputFilter;
import kr.poturns.virtualpalace.input.OperationInputDetector;

/**
 * Created by Myungjin Kim on 2015-07-30.
 */
public class WearInputDetector extends OperationInputDetector<Object> {

    private WearInputConnector mConnector;

    public WearInputDetector(Context context) {
        super(context);
        init();
    }

    public WearInputDetector(Context context, IOperationInputFilter<Object> filter) {
        super(context, filter);
        init();
    }

    private void init() {
        mConnector = new WearInputConnector(getContext());
    }




}
