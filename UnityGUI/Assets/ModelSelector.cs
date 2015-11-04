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
        UITransform uiTransform = transform.parent.gameObject.GetComponent<UITransform>();
        GameObject Target = uiTransform.TargetObj; 
		if (Target != null) 
		{
            //UITransform UItemp = transform.parent.gameObject.GetComponent<UITransform> ();
            BookCaseScript bookCaseScript = Target.GetComponent<BookCaseScript>();
            bookCaseScript.SetKind(uiTransform.CurrentType);
            bookCaseScript.SetCurrentPrefab (ObjType);
            bookCaseScript.CreateBook(); 
			StateManager.GetManager().SwitchState(new VRSceneIdleState(StateManager.GetManager()));

            uiTransform.OnOffOUIButton (false);
            uiTransform.UnlockCameraRot();

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
