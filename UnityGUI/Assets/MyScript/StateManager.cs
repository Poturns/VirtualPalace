using UnityEngine;
using MyScript.States;
using MyScript.Interface;
using AndroidApi.Controller;
using System;
using System.Text;
using BridgeApi.Controller;
using BridgeApi.Controller.Request;

public class StateManager : MonoBehaviour, IPlatformBridge
{

    public const string SCENE_MAIN = "MyTest";
    public const string SCENE_VR = "VRWorld";
    public const string SCENE_AR = "ARScene";


    private static StateManager instanceRef;


    private Utils.AsyncTasker Tasker;
    private IStateBase activeState;

    private IPlatformBridgeDelegate bridgeDelegate;


    public static StateManager GetManager()
    {
        return instanceRef;
    }

    void Awake()
    {
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

        if(activeState is ISceneChangeState)
        {
            ((ISceneChangeState)activeState).OnSceneChanged();
        }

    }

    void Start()
    {
        Debug.Log("Start StateM");
        activeState = new BeginState(this);
        ((ISceneChangeState)activeState).OnSceneChanged();
    }

    // Update is called once per frame
    void Update()
    {
        Tasker.OnUpdate();
		
        if (activeState != null)
            activeState.StateUpdate();
    }

    void OnGUI()
    {
        if (activeState != null)
            activeState.ShowIt();
    }


    public void SwitchState(IStateBase newState)
    {
        //UI SHOW Object Delete

        activeState = newState;
    }

    public static void InputTextMesh(TextMesh tm, string T)
    {
        int StrSize = T.Length;
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < StrSize)
        {
            if (i % 17 == 0 && i > 0) sb.Append('\n');
            sb.Append(T[i]);
            i++;
        }
        sb.Append('\n');
        tm.text = sb.ToString();
    }

    public void QueueOnMainThread(Action a)
    {
        Tasker.QueueOnMainThread(a);
    }

    public static void SwitchScene(string sceneName)
    {
        switch (sceneName)
        {
            case SCENE_MAIN:
                GetManager().SwitchState(new BeginState(GetManager()));

                break;

            case SCENE_VR:
                GetManager().SwitchState(new VRSceneIdleState(GetManager()));
                break;

            case SCENE_AR:
                //GetManager().SwitchState(new AR(GetManager()));
                break;
            default:
                return;

        }

        Application.LoadLevel(sceneName);
    }

    #region Base Platform Method (Android, IOS, ....)


    private void InitPlatformBridge()
    {

        if (Application.platform == RuntimePlatform.Android)
        {
            bridgeDelegate = new AndroidUnityBridge();
            Debug.Log("UnityBridge - AndroidUnityBridge attatched sucessfully.");
        }
        else
        {
            bridgeDelegate = null;
            Debug.LogWarning("UnityBridge - failed to attatch.");
        }
    }


    /// <summary>
    /// 기저 플랫폼의 Controller로부터 전달되어온 Input Message를 처리한다.
    /// </summary>
    /// <param name="json">Controller에서 전달된 Input Message json</param>
    public void HandleInputsFromController(string json)
    {
        Debug.Log(json);
        if (activeState != null)
        {
            Debug.Log("activeState : " + activeState);
            activeState.InputHandling(JsonInterpreter.ParseInputCommands(json));
        }
        else
        {
            Debug.LogWarning("activeState == null !!!");
        }
    }

    /// <summary>
    /// 기저 플랫폼의 Controller로 부터 전달되어온 일반 Message를 처리한다.
    /// </summary>
    /// <param name="json">Controller에서 전달된 일반 Message json</param>
    public void HandleMessageFromController(string json)
    {
        Debug.Log(json);

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
    public bool SendSingleMessageToPlatform(string jsonMessage)
    {
        return bridgeDelegate != null && bridgeDelegate.SendSingleMessageToPlatform(jsonMessage);
    }

    #endregion Base Platform Method (Android, IOS, ....)

}
