using UnityEngine;
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
        //Debug.Log("=============== SceneMove to : " + SceneName);
        //StateManager.SwitchScene(SceneName);

        UnityScene scene;
        if (name.Contains("VR"))
            scene = UnityScene.VR;
        else if (name.Contains("AR"))
            scene = UnityScene.AR;
        else
            scene = UnityScene.Lobby;

        Debug.Log("=============== SceneMove to : [" + scene + "]");
        StateManager.SwitchScene(scene);
    }

}
