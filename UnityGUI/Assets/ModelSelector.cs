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
        GameObject EventSys = GameObject.Find("EventSystem");
        if (EventSys == null) Debug.Log("Event System Find Fail");
        //else Debug.Log(EventSys);

        GazeInputModule SelectModule = EventSys.GetComponent<GazeInputModule>();
        if (SelectModule == null) Debug.Log("GazeInputModule == null");
        SelectModule.Mode = 0;
    }
}
