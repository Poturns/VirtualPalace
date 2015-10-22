using UnityEngine;
using System.Collections;
using MyScript.States;
using MyScript.Interface;

public class SceneChange : MonoBehaviour, IRaycastedObject
{
    public string SceneName;

    public void OnSelect()
    {
        SceneMove();
    }

    public void SceneMove()
    {
        Debug.Log("SceneMove : " + SceneName);
        StateManager.SwitchScene(SceneName);
    }

}
