using UnityEngine;
using System.Collections;
using MyScript;
using MyScript.States;
using MyScript.Interface;

public class ModelSelector : MonoBehaviour,IRaycastedObject {

	public OBJ_LIST ObjType;
	public void OnSelect()
	{
		Debug.Log ("Call Select");
		GameObject Target = transform.parent.gameObject.GetComponent<UITransform> ().TargetObj; 
		if (Target != null) 
		{
			UITransform UItemp = transform.parent.gameObject.GetComponent<UITransform> ();
			Target.GetComponent<BookCaseScript> ().SetKind(UItemp.CurrentType);
			Target.GetComponent<BookCaseScript> ().SetCurrentPrefab (ObjType);
			Target.GetComponent<BookCaseScript> ().CreateBook(); 
			StateManager.GetManager().SwitchState(new VRSceneIdleState(StateManager.GetManager()));

			UItemp.OnOffOUIButton (false);
			UItemp.UnlockCameraRot();
			//Target.GetComponent<BookCaseScript> ().
		}
			
		/*
		UITransform ModelUI = GameObject.Find ("ObjModelSelectUI").GetComponent<UITransform> ();
		ModelUI.CurrentType = ThisType;
		ModelUI.OnOffOUIButton (true);
		transform.parent.gameObject.GetComponent<UITransform> ().OnOffOUIButton (false);
*/
	}
}
