package kr.poturns.virtualpalace.controller;


import android.location.Location;
import android.util.Pair;

import java.security.InvalidParameterException;
import java.util.Stack;

import kr.poturns.virtualpalace.InfraDataService;
import kr.poturns.virtualpalace.augmented.AugmentedCore;
import kr.poturns.virtualpalace.controller.data.SceneLifeCycle;

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

    protected AugmentedCore mAugmentedModule;

    protected PalaceEngine(PalaceApplication application) {
        super(application);

        UnitySceneStack = new Stack<Pair<String, SceneLifeCycle>>();
    }


    @Override
    protected void destroy() {

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

        switch (cycle) {
            case onCreate:
                UnitySceneStack.push(new Pair<String, SceneLifeCycle>(scene, cycle));

                if (SceneLifeCycle.LOBBY.equalsIgnoreCase(scene)) {
                    //

                } else if (SceneLifeCycle.AR.equalsIgnoreCase(scene)) {
                    // Sensor 데이터 수집 시작
                    App.getInfraDataService().startListeningForAR();
                    mAugmentedModule = new AugmentedCore(App);
                    mAugmentedModule.init(1280, 960);


                } else if (SceneLifeCycle.VR.equalsIgnoreCase(scene)) {
                    // Google App-Drive Connect
                    AppDriveAssistant.connect();
                }
                break;

            case onLoaded:
                UnitySceneStack.push(new Pair<String, SceneLifeCycle>(scene, cycle));

                if (SceneLifeCycle.LOBBY.equalsIgnoreCase(scene)) {
                    //

                } else if (SceneLifeCycle.AR.equalsIgnoreCase(scene)) {
                    mAugmentedModule.start();

                } else if (SceneLifeCycle.VR.equalsIgnoreCase(scene)) {
                    //

                }
                break;

            case onPaused:
                UnitySceneStack.remove(new Pair<String, SceneLifeCycle>(scene, SceneLifeCycle.onLoaded));

                if (SceneLifeCycle.LOBBY.equalsIgnoreCase(scene)) {
                    //

                } else if (SceneLifeCycle.AR.equalsIgnoreCase(scene)) {
                    mAugmentedModule.stop();

                } else if (SceneLifeCycle.VR.equalsIgnoreCase(scene)) {
                    //
                }
                break;

            case onDestroy:
                UnitySceneStack.remove(new Pair<String, SceneLifeCycle>(scene, SceneLifeCycle.onCreate));

                if (SceneLifeCycle.LOBBY.equalsIgnoreCase(scene)) {
                    destroy();

                } else if (SceneLifeCycle.AR.equalsIgnoreCase(scene)) {
                    // Sensor 데이터 수집 중지
                    App.getInfraDataService().stopListeningFromAR();

                } else if (SceneLifeCycle.VR.equalsIgnoreCase(scene)) {
                    // Google App-Drive Disconnect
                    AppDriveAssistant.destroy();
                }
                break;
        }
    }

    public Pair<String, SceneLifeCycle> getCurrentLifeCycle() {

        return UnitySceneStack.peek();
    }
}
