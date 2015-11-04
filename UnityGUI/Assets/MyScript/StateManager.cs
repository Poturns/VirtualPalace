using UnityEngine;
using MyScript.States;
using MyScript.Interface;
using AndroidApi.Controller;
using System;
using BridgeApi.Controller;
using BridgeApi.Controller.Request;

public enum UnityLifeCycle
{
    onCreate,
    onLoaded,
    onPaused,
    onDestroy
}


public enum UnityScene
{
    Lobby,
    VR,
    AR
}

public static class UnitySceneExtend
{
    public const string SCENE_LOBBY = "MyTest";
    public const string SCENE_VR = "VRWorld";
    public const string SCENE_AR = "ARScene";

    public static string SceneName(this UnityScene scene)
    {
        switch (scene)
        {
            case UnityScene.Lobby:
                return SCENE_LOBBY;

            case UnityScene.VR:
                return SCENE_VR;

            case UnityScene.AR:
                return SCENE_AR;

            default:
                return "";
        }
    }


    public static UnityScene GetByName(string name)
    {
        switch (name)
        {
            default:
            case SCENE_LOBBY:
                return UnityScene.Lobby;
            case SCENE_AR:
                return UnityScene.AR;
            case SCENE_VR:
                return UnityScene.VR;
        }
    }
}



public class StateManager : MonoBehaviour, IPlatformBridge
{

    private static StateManager instanceRef;


    private Utils.AsyncTasker Tasker;
    private IStateBase activeState;

    private IPlatformBridgeDelegate bridgeDelegate;


    public static StateManager GetManager()
    {
        return instanceRef;
    }
    public int ObjCount;
    void Awake()
    {
        Debug.Log("=== Awake ===");
        if (instanceRef == null)
        {
            InitPlatformBridge();
            Tasker = new Utils.AsyncTasker();
            instanceRef = this;
            DontDestroyOnLoad(gameObject);
        }
        else
        {
            DestroyImmediate(gameObject);
        }

    }

    void OnLevelWasLoaded(int level)
    {
        ISceneChangeState currentSceneState = activeState as ISceneChangeState;
        if (currentSceneState != null)
        {
            currentSceneState.OnSceneChanged();
            SendLifeCyleMessage(currentSceneState, UnityLifeCycle.onLoaded);
        }

		if (level == 1) 
		{
			SaveLoader Saver = GameObject.Find ("_Script").GetComponent<SaveLoader> ();
			if (Saver == null)
				Debug.Log ("SaveLoaderFindFail");
			else
				Saver.LoadToFile ();
		}
    }

    void Start()
    {
        Debug.Log("=== Start StateManager ===");
        BeginState beginState = new BeginState(this);
        activeState = beginState;

        SendLifeCyleMessage(beginState, UnityLifeCycle.onCreate);
        OnLevelWasLoaded(0);
    }

    // Update is called once per frame
    void Update()
    {
        Tasker.OnUpdate();

        if (activeState != null) activeState.StateUpdate();
    }

    void OnGUI()
    {
        if (activeState != null) activeState.ShowIt();
    }


    public void SwitchState(IStateBase newState)
    {
        //UI SHOW Object Delete

        activeState = newState;
    }

    /// <summary>
    /// 작업을 Unity의 MainThread에서 수행한다.<para/>
    /// </summary>
    /// <param name="a">Unity MainThread에서 실행시킬 작업</param>
    /// <param name="runImmediatelyIfMainThread">이 메소드를 호출한 Thread가 MainThread이면 즉각적으로 실행시킬지 여부</param>
    public void QueueOnMainThread(Action a, bool runImmediatelyIfMainThread)
    {
        Tasker.QueueOnMainThread(a, runImmediatelyIfMainThread);
    }

    /// <summary>
    /// 작업을 Unity의 MainThread에서 수행한다.<para/>
    /// 이 메소드를 호출한 Thread가 MainThread라면 그대로 실행시킨다.
    /// </summary>
    /// <param name="a">Unity MainThread에서 실행시킬 작업</param>
    public void QueueOnMainThread(Action a)
    {
        Tasker.QueueOnMainThread(a);
    }

    public static void SwitchScene(UnityScene unityScene)
    {
        StateManager manager = GetManager();
        ISceneChangeState newSceneState;
        switch (unityScene)
        {
            case UnityScene.Lobby:
                newSceneState = new BeginState(manager);
                break;

            case UnityScene.VR:
                newSceneState = new VRSceneIdleState(manager);
                break;

            case UnityScene.AR:
                //GetManager().SwitchState(new AR(manager));
                return;
            default:
                return;
        }


        ISceneChangeState currentState = manager.activeState as ISceneChangeState;
        if (currentState != null)
            manager.SendLifeCyleMessage(currentState, UnityLifeCycle.onPaused);

        manager.SendLifeCyleMessage(newSceneState, UnityLifeCycle.onCreate);
        manager.SwitchState(newSceneState);

        //Application.LoadLevel(unityScene.SceneName());
        Application.LoadLevel((int)unityScene);
    }

    internal void SendLifeCyleMessage(ISceneChangeState sceneState, UnityLifeCycle lifeCycle)
    {
        string json = JsonInterpreter.MakeUnityLifeCycleMessage(sceneState.UnitySceneID, lifeCycle);
        Debug.Log("=============== LifeCycle : " + json);
        SendSingleMessageToPlatform(json);
    }

    #region Base Platform Method (Android, IOS, ....)


    private void InitPlatformBridge()
    {

        if (Application.platform == RuntimePlatform.Android)
        {
            bridgeDelegate = new AndroidUnityBridge();
            Debug.Log("=============== UnityBridge - AndroidUnityBridge attatched sucessfully.");
        }
        else
        {
            bridgeDelegate = null;
            Debug.LogWarning("=============== UnityBridge - failed to attatch.");
        }
    }


    /// <summary>
    /// 기저 플랫폼의 Controller로부터 전달되어온 Input Message를 처리한다.
    /// </summary>
    /// <param name="json">Controller에서 전달된 Input Message json</param>
    public void HandleInputsFromController(string json)
    {
        Debug.Log("=============== " + json);
        if (activeState != null)
        {
            Debug.Log("=============== Current ActiveState : " + activeState);
            activeState.InputHandling(JsonInterpreter.ParseInputCommands(json));
        }
        else
        {
            Debug.LogWarning("=============== ActiveState == null !!!");
        }
    }

    /// <summary>
    /// 기저 플랫폼의 Controller로 부터 전달되어온 일반 Message를 처리한다.
    /// </summary>
    /// <param name="json">Controller에서 전달된 일반 Message json</param>
    public void HandleMessageFromController(string json)
    {
        Debug.Log("=============== " + json);

        //TODO handle message
    }

    //TODO 이하 메소드는 Delegate 객체로 처리하기
    /// <summary>
    /// UNITY에서 기저 Platform에 요청을 보낸다.
    /// </summary>
    /// <param name="jsonMessage">요청의 세부 사항이 Json형태로 기술되어 있는 문자열</param>
    /// <param name="callback">요청에 대한 응답을 받을 콜백</param>
    /// <returns>요청이 접수되었을 경우, TRUE</returns>
    public bool RequestToPlatform(IRequest request, Action<string> callback)
    {
        return bridgeDelegate != null && bridgeDelegate.RequestToPlatform(request, callback);
    }

    /// <summary>
    /// 기저 Platform에서 요청한 ID 에 해당하는 결과를 콜백메소드로 반환한다.
    /// </summary>
    /// <param name="id">콜백의 id</param>
    /// <param name="jsonResult">요청에 대한 결과값이 Json형태로 기술된 문자열</param>
    public void RespondToPlatform(long id, string jsonResult)
    {
        if (bridgeDelegate != null) bridgeDelegate.RespondToPlatform(id, jsonResult);
    }

    /// <summary>
    /// UNITY 에서 단일 메시지를 기저 Platform으로 전송한다.
    /// </summary>
    /// <param name="jsonMessage">전송할 Json 메시지</param>
    /// <returns>메시지가 정상적으로 전송되었을 때, TRUE</returns>
    public void SendSingleMessageToPlatform(string jsonMessage)
    {
        if (bridgeDelegate != null) bridgeDelegate.SendSingleMessageToPlatform(jsonMessage);
    }

    #endregion Base Platform Method (Android, IOS, ....)

}
