package kr.poturns.virtualpalace.input;

import android.app.Application;
import android.os.Handler;

/**
 * <b>INPUT 모듈과 연결된 CONTROLLER 의 핸들러를 반환하기 위한 APPLICATION 클래스</b>
 *
 * @author Yeonho.Kim
 */
public abstract class GlobalApplication extends Application {

    public abstract Handler getInputHandler(int supportType);

    public abstract Handler setInputConnector(int supportType, OperationInputConnector connector);
}
