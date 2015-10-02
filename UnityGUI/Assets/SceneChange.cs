using UnityEngine;
using System.Collections;
using MyScript.States;
using MyScript.Interface;
using MyScript;

public class SceneChange : MonoBehaviour , IRaycastedObject
{
	public string SceneName;

	public void OnSelect ()
	{
        SceneMove();
	}

	public void SceneMove ()
	{
		StateManager.SwitchScene (SceneName);
	}

}
