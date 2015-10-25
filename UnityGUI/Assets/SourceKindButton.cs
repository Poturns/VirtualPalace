using UnityEngine;
using System.Collections;
using MyScript;
using MyScript.States;
using MyScript.Interface;

enum KIND_SOURCE
{
	TEXT,
	IMAGE,
	MOVIE
};
public class SourceKindButton : MonoBehaviour,IRaycastedObject {

	public void OnSelect()
	{
		//StateManager.GetManager().SwitchState(new VRModelSelect(StateManager.GetManager() ,))
	}
}
