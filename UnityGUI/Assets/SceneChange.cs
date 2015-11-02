using UnityEngine;
using System.Collections;
using MyScript.States;
using MyScript.Interface;
using MyScript;

public class SceneChange :  AbstractBasicObject
{
    public string SceneName;
	void Awake()
	{
		SourceKind = KIND_SOURCE.UI_BUTTON;
		ModelKind = OBJ_LIST.NO_MODEL;
	}
	public override void OnSelect()
    {
        SceneMove();
    }

    public void SceneMove()
    {
        Debug.Log("=============== SceneMove to : " + SceneName);
        StateManager.SwitchScene(SceneName);
    }

}
