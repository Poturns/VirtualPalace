using UnityEngine;
using System.Collections;
using MyScript;
using MyScript.States;
using MyScript.Interface;

public enum KIND_SOURCE
{
	TEXT,
	IMAGE,
	MOVIE
};
public class SourceKindButton : MonoBehaviour,IRaycastedObject {
	public KIND_SOURCE ThisType;
	public void OnSelect()
	{
		UITransform ModelUI = GameObject.Find ("ObjModelSelectUI").GetComponent<UITransform> ();
		ModelUI.CurrentType = ThisType;
		ModelUI.OnOffOUIButton (true);
		ModelUI.LockCameraRot ();
		transform.parent.gameObject.GetComponent<UITransform> ().OnOffOUIButton (false);
	}
}
