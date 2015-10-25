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
    /// ���� �÷����� Controller�κ��� ���޵Ǿ�� Input Message�� ó���Ѵ�.
    /// </summary>
    /// <param name="json">Controller���� ���޵� Input Message json</param>
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
    /// ���� �÷����� Controller�� ���� ���޵Ǿ�� �Ϲ� Message�� ó���Ѵ�.
    /// </summary>
    /// <param name="json">Controller���� ���޵� �Ϲ� Message json</param>
    public void HandleMessageFromController(string json)
    {
        Debug.Log(json);

        //TODO handle message
    }

    //TODO ���� �޼ҵ�� Delegate ��ü�� ó���ϱ�
    /// <summary>
    /// UNITY���� ���� Platform�� ��û�� ������.
    /// </summary>
    /// <param name="jsonMessage">��û�� ���� ������ Json���·� ����Ǿ� �ִ� ���ڿ�</param>
    /// <param name="callback">��û�� ���� ������ ���� �ݹ�</param>
    /// <returns>��û�� �����Ǿ��� ���, TRUE</returns>
    public bool RequestToPlatform(IRequest request, Action<string> callback)
    {
        return bridgeDelegate != null && bridgeDelegate.RequestToPlatform(request, callback);
    }

    /// <summary>
    /// ���� Platform���� ��û�� ID �� �ش��ϴ� ����� �ݹ�޼ҵ�� ��ȯ�Ѵ�.
    /// </summary>
    /// <param name="id">�ݹ��� id</param>
    /// <param name="jsonResult">��û�� ���� ������� Json���·� ����� ���ڿ�</param>
    public void RespondToPlatform(long id, string jsonResult)
    {
        if (bridgeDelegate != null) bridgeDelegate.RespondToPlatform(id, jsonResult);
    }

    /// <summary>
    /// UNITY ���� ���� �޽����� ���� Platform���� �����Ѵ�.
    /// </summary>
    /// <param name="jsonMessage">������ Json �޽���</param>
    /// <returns>�޽����� ���������� ���۵Ǿ��� ��, TRUE</returns>
    public bool SendSingleMessageToPlatform(string jsonMessage)
    {
        return bridgeDelegate != null && bridgeDelegate.SendSingleMessageToPlatform(jsonMessage);
    }

    #endregion Base Platform Method (Android, IOS, ....)

}
