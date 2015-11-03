using UnityEngine;

namespace MyScript.States
{
    [System.Obsolete()]
    public class VRMemoViewExit : AbstractGazeInputState
	{
		private GameObject UIMemoBG;
		private GameObject UIMemoTxt;
        private GameObject TargetObject;
     
        public VRMemoViewExit (StateManager managerRef, GameObject TargetObject) : base(managerRef, "VRMemoViewExit")
		{
            this.TargetObject = TargetObject;

            DestroyMarkedObject();

            UIMemoBG = GameObject.Find ("MemoView");
			if (!UIMemoBG)
				Debug.Log ("Sel Target is Null");
			UIMemoBG.GetComponent<SpriteRenderer> ().enabled = false;
			
			UIMemoTxt = GameObject.Find ("MemoText");
			if (!UIMemoTxt)
				Debug.Log ("Sel Text is Null");
			
			//TargetObject.GetComponent<MemoObject>().MemoPrefab;
			TextMesh T = UIMemoTxt.GetComponent<TextMesh> ();
			T.text = "";

			SetGazeInputMode (GAZE_MODE.OBJECT);
			SetCameraLock (false);
			//GameObject.Find ("Head").GetComponent<CardboardHead> ().ViewMoveOn = false;

			//manager.SwitchState (new VRObjectView (managerRef, TargetObject));
			
		}

		public override void StateUpdate()
		{
            SwitchState(new VRObjectView(Manager, TargetObject));
        }
				
	}
}

