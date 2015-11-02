using UnityEngine;

namespace MyScript.States
{
    public class VRModelSelect : AbstractGazeInputState
	{
		//BookCaseTrigger
		private GameObject Target;

		private UITransform UITrans;
		public VRModelSelect (StateManager managerRef , GameObject TargetObject) : base(managerRef, "VRModelSelectState")
		{
			Target = TargetObject;

            SetGazeInputMode(GAZE_MODE.UI);

			GameObject UI = GameObject.Find ("ModelSelectUI");
			UI.GetComponent<UITransform> ().OnOffOUIButton (true);

			UITrans = UI.GetComponent<UITransform> (); 
			UITrans.LockCameraRot ();
		}

        protected override void HandleCancelOperation()
        {
            base.HandleCancelOperation();
			EndState();
        }

		public void EndState()
		{
			UITrans.UnlockCameraRot ();
			UITrans.OnOffOUIButton (false);
			UITransform ModelUI = GameObject.Find ("ObjModelSelectUI").GetComponent<UITransform>();
			ModelUI.UnlockCameraRot ();
			ModelUI.OnOffOUIButton (false);
			SetGazeInputMode (GAZE_MODE.OBJECT);
			SwitchState (new VRSceneIdleState (Manager));
		
		}
		
	}
}

