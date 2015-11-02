using UnityEngine;
using System.Collections;
using MyScript;
using MyScript.States;
using MyScript.Interface;

public class ModelSelector : AbstractBasicObject {

	public OBJ_LIST ObjType;
	public override void OnSelect()
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

            RestoreGazeSelectMode();
			//Target.GetComponent<BookCaseScript> ().
		}
			
		/*
		UITransform ModelUI = GameObject.Find ("ObjModelSelectUI").GetComponent<UITransform> ();
		ModelUI.CurrentType = ThisType;
		ModelUI.OnOffOUIButton (true);
		transform.parent.gameObject.GetComponent<UITransform> ().OnOffOUIButton (false);
*/
	}

    private void RestoreGazeSelectMode()
    {
        GameObject Gaze = GameObject.Find("GazePointer");
		if (Gaze == null) Debug.Log("GazePointer Find Fail");
        //else Debug.Log(EventSys);

		GazeCusor cursor = Gaze.GetComponent<GazeCusor>();
		if (cursor == null) Debug.Log("GazeCusor == null");
		cursor.Mode = GAZE_MODE.OBJECT;
    }
}
