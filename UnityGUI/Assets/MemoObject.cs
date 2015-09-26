using UnityEngine;
using System.Collections;
using UnityEngine.UI;
using MyScript.States;

public class MemoObject : MonoBehaviour 
{

	public string Title {
		get;
		set;
	}
	private string Memo;
	public GameObject UIObject;

	public void InputMemo(string Text)
	{
		Memo = Text;
	}
	public string GetMemo()
	{
		return Memo;
	}
	public void ObjectViewExit()
	{
		StateManager.GetManager ().SwitchState (new VRObjectViewExitState (StateManager.GetManager ()));
	}
	public void ClickObj()
	{
		StateManager.GetManager().SwitchState (new VRObjectView(StateManager.GetManager(),gameObject));
	}
	// Use this for initialization
	void Start () {
		Memo = "Test Memo String";
		Title = "Test Title";
	}
	
	// Update is called once per frame
	void Update () {
	
	}
}
