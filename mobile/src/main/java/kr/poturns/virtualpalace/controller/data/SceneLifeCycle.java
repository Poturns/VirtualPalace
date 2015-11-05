package kr.poturns.virtualpalace.controller.data;

/**
 *
 */
public enum SceneLifeCycle {
    /**
     * Scene 생성 중 : Scene 생성시 필요한 데이터를 전달받아 Scene을 구축하는데 사용하도록 한다.
     */
    onCreate,
    /**
     * Scene이 사용자와 인터랙션할 준비가 됨 : 사용자 인터랙션에 필요한 데이터/이벤트를 전달받을 수 있도록 준비한다.
     */
    onLoaded,
    /**
     * Scene이 사용자와 인터랙션을 중지 : 사용자 인터랙션에 필요한 데이터/이벤트를 전달받지 않도록 한다.
     */
    onPaused,
    /**
     * Scene이 종료되었음을 알리고, 이 Scene을 위해 할당되었던 자원을 해제할 수 있도록 한다.
     */
    onDestroy;


    public static final String LOBBY = "lobby";

    public static final String AR = "ar";

    public static final String VR = "vr";
}
