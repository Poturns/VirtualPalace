using UnityEngine;
using MyScript.States;
using MyScript.Interface;

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
	public void SwitchState(IStateBase newState)
	{
		activeState = newState;
	}
}
