using UnityEngine;
using System.Collections.Generic;
using MyScript.States;
using MyScript.Interface;
using AndroidApi.Controller;
using System;
using System.Text;

public class StateManager : MonoBehaviour
{

    public const string SCENE_MAIN = "MyTest";
    public const string SCENE_VR = "VRWorld";
    public const string SCENE_AR = "ARScene";



    private static readonly Utils.AsyncTasker Tasker = new Utils.AsyncTasker();
    private IStateBase activeState;
    private static StateManager instanceRef;

    public static StateManager GetManager()
    {
        return instanceRef;
    }
    void Awake()
    {
        if (instanceRef == null)
        {
            instanceRef = this;
            DontDestroyOnLoad(gameObject);
        }
        else
        {
            DestroyImmediate(gameObject);
        }

        AndroidUnityBridge.ClearEventHandler();
        AndroidUnityBridge.InputReceivedEvent += InputControlFunc;

    }

    void Start()
    {
        Debug.Log("Start StateM");
        activeState = new BeginState(this);
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

    public void InputControlFunc(List<Operation> InputOp)
    {
        Tasker.QueueOnMainThread(() =>
        {
            if (activeState != null)
                activeState.InputHandling(InputOp);
        });
    }

	public void SwitchState(IStateBase newState)
	{
		//UI SHOW Object Delete

		activeState = newState;
	}
	public static void InputTextMesh(TextMesh tm ,string T )
	{
		int StrSize = T.Length;
        StringBuilder sb = new StringBuilder();
		int i = 0 ; 
		while(i < StrSize)
		{
			if(i%17 == 0 && i > 0) sb.Append('\n');
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
}
