package kr.poturns.virtualpalace.inputmodule.gaze;

import android.content.Context;

import kr.poturns.virtualpalace.input.IProcessorCommands;
import kr.poturns.virtualpalace.input.OperationInputConnector;

/**
 * Created by Yeonho on 2015-11-05.
 */
public class GazeInputConnector extends OperationInputConnector {


    public GazeInputConnector(Context context) {
        super(context, IProcessorCommands.TYPE_INPUT_SUPPORT_GAZE);

    }
}
