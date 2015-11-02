using UnityEngine;
using System.Collections;
using MyScript;
using MyScript.States;
using MyScript.Interface;


public class SourceKindButton : AbstractBasicObject {
	public KIND_SOURCE ThisType;
	public override void OnSelect()
	{
		UITransform ModelUI = GameObject.Find ("ObjModelSelectUI").GetComponent<UITransform> ();
		ModelUI.CurrentType = ThisType;
		ModelUI.OnOffOUIButton (true);
		ModelUI.LockCameraRot ();
		transform.parent.gameObject.GetComponent<UITransform> ().OnOffOUIButton (false);
	}
}
