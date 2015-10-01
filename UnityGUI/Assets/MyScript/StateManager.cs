using UnityEngine;
using MyScript.States;
using MyScript.Interface;
using AndroidApi.Controller;

public class StateManager : MonoBehaviour 
{
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
			DontDestroyOnLoad (gameObject);
			AndroidUnityBridge.GetInstance().OnInputReceived += InputControlFunc;
		} 
		else 
		{
			DestroyImmediate(gameObject);
		}
	}
	void Start () 
	{
		Debug.Log ("Start StateM");
		activeState = new BeginState (this);
	}
	
	// Update is called once per frame
	void Update () {
		if (activeState != null)
			activeState.StateUpdate ();
	}
	void OnGUI()
	{
		if (activeState != null)
			activeState.ShowIt ();
	}
	public void InputControlFunc(Operation[] InputOp)
	{
		activeState.InputHandling (InputOp);
	}
	public void SwitchState(IStateBase newState)
	{
		activeState = newState;
	}
}
