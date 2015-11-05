package kr.poturns.virtualpalace.controller;


import android.util.DisplayMetrics;
import android.util.Pair;

import java.security.InvalidParameterException;
import java.util.Stack;

import kr.poturns.virtualpalace.augmented.AugmentedManager;
import kr.poturns.virtualpalace.controller.data.SceneLifeCycle;
import kr.poturns.virtualpalace.inputmodule.gaze.GazeInputConnector;

/**
 * <b> MIDDLE CONTROLLER : 컨트롤러의 흐름 제어 기능을 다룬다 </b>
 * <p>
 *     -
 * <p/>
 *
 * @author Yeonho.Kim
 */
abstract class PalaceEngine extends PalaceCore {

    protected final Stack<Pair<String, SceneLifeCycle>> UnitySceneStack;

    protected AugmentedManager mAugmentedModule;

    private GazeInputConnector mGazeInputConnector;

    protected PalaceEngine(PalaceApplication application) {
        super(application);

        UnitySceneStack = new Stack<Pair<String, SceneLifeCycle>>();
    }


    @Override
    protected void destroy() {
        onLifeCycle(SceneLifeCycle.LOBBY, SceneLifeCycle.onDestroy);
        UnitySceneStack.clear();

        super.destroy();
    }


    /**
     * Unity Scene 의 LifeCycle 변화에 대응한다.
     *
     * @param scene {@link SceneLifeCycle#LOBBY} or {@link SceneLifeCycle#AR} or {@link SceneLifeCycle#VR}
     * @param cycle {@link SceneLifeCycle}
     */
    protected void onLifeCycle(String scene, SceneLifeCycle cycle) {
        if (scene == null || cycle == null)
            throw new InvalidParameterException();

        Pair<String, SceneLifeCycle> pointer;
        switch (cycle) {
            case onCreate:
                UnitySceneStack.push(new Pair<String, SceneLifeCycle>(scene, cycle));

                if (SceneLifeCycle.LOBBY.equalsIgnoreCase(scene)) {
                    // Google App-Drive Connect
                    //AppDriveAssistant.connect();

                    // Gaze Input 초기 활성화
                    mGazeInputConnector = new GazeInputConnector(App);
                    activateInputConnector(mGazeInputConnector.getSupportType());

                } else if (SceneLifeCycle.AR.equalsIgnoreCase(scene)) {
                    // Sensor 데이터 수집 시작
                    App.getInfraDataService().startListeningForAR();
                    mAugmentedModule = AugmentedManager.getInstance(App);

                    DisplayMetrics metrics = App.getResources().getDisplayMetrics();
                    int screenSizeX = metrics.widthPixels;
                    int screenSizeY = metrics.heightPixels;
                    mAugmentedModule.init(screenSizeX, screenSizeY);

                } else if (SceneLifeCycle.VR.equalsIgnoreCase(scene)) {

                }
                break;

            case onLoaded:
                String topScene = UnitySceneStack.peek().first;
                if (!SceneLifeCycle.LOBBY.equalsIgnoreCase(topScene) && topScene != scene) {
                    // onDestroy LifeCycle 메시지를 수신하지 못하였을 때,
                    // 이전 Scene의 onDestroy를 처리한다.
                    onLifeCycle(topScene, SceneLifeCycle.onDestroy);
                }

                UnitySceneStack.push(new Pair<String, SceneLifeCycle>(scene, cycle));

                if (SceneLifeCycle.LOBBY.equalsIgnoreCase(scene)) {
                    //

                } else if (SceneLifeCycle.AR.equalsIgnoreCase(scene)) {
                    mAugmentedModule.start();

                } else if (SceneLifeCycle.VR.equalsIgnoreCase(scene)) {
                    //

                }
                break;

            case onPaused: {
                boolean result = UnitySceneStack.remove(new Pair<String, SceneLifeCycle>(scene, SceneLifeCycle.onLoaded));
                if (!result)
                    return;

                if (SceneLifeCycle.LOBBY.equalsIgnoreCase(scene)) {
                    //

                } else if (SceneLifeCycle.AR.equalsIgnoreCase(scene)) {
                    mAugmentedModule.stop();

                } else if (SceneLifeCycle.VR.equalsIgnoreCase(scene)) {
                    //
                }
            } break;

            case onDestroy: {
                boolean result = UnitySceneStack.remove(new Pair<String, SceneLifeCycle>(scene, SceneLifeCycle.onCreate));
                if (!result)
                    return;

                if (SceneLifeCycle.LOBBY.equalsIgnoreCase(scene)) {
                    // Google App-Drive Disconnect
                   // AppDriveAssistant.destroy();

                    if (mGazeInputConnector != null)
                        mGazeInputConnector.disconnect();

                    destroy();

                } else if (SceneLifeCycle.AR.equalsIgnoreCase(scene)) {
                    // Sensor 데이터 수집 중지
                    App.getInfraDataService().stopListeningFromAR();

                } else if (SceneLifeCycle.VR.equalsIgnoreCase(scene)) {

                }
            } break;
        }
    }

    public Pair<String, SceneLifeCycle> getCurrentLifeCycle() {
        return UnitySceneStack.peek();
    }
}
