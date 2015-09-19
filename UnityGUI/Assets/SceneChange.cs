using UnityEngine;
using System.Collections;
using MyScript.States;

public class SceneChange : MonoBehaviour {
	public string SceneName;
	public void SceneMove()
	{
		StateManager.GetManager().SwitchState (new VRSceneIdleState(StateManager.GetManager()));
		Application.LoadLevel (SceneName);
	}

}
