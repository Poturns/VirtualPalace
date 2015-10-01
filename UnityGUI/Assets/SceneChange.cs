using UnityEngine;
using System.Collections;
using MyScript.States;
using MyScript.Interface;

public class SceneChange : MonoBehaviour , IObject {
	public string SceneName;
	public void OnSelect()
	{
		SceneMove ();
	}
	public void SceneMove()
	{
		StateManager.GetManager().SwitchState (new VRSceneIdleState(StateManager.GetManager()));
		Application.LoadLevel (SceneName);
	}

}
