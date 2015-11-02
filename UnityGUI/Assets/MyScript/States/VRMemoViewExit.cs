using UnityEngine;

namespace MyScript.States
{
    public class VRMemoViewExit : AbstractGazeInputState
	{
		private GameObject UIMemoBG;
		private GameObject UIMemoTxt;
        private GameObject TargetObject;

        public VRMemoViewExit (StateManager managerRef, GameObject TargetObject) : base(managerRef, "VRMemoViewExit")
		{
            this.TargetObject = TargetObject;
			
            GameObject DisposolObj = GameObject.FindGameObjectWithTag("Disposol");
            if (DisposolObj) GameObject.Destroy(DisposolObj);

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

			SetGazeInputMode (0);
			SetCameraLock (false);
			//GameObject.Find ("Head").GetComponent<CardboardHead> ().ViewMoveOn = false;

			//manager.SwitchState (new VRObjectView (managerRef, TargetObject));
			
		}

		public override void StateUpdate()
		{
            SwitchState(new VRObjectView(Manager, TargetObject));
        }
		
		void Switch()
		{
			//Application.LoadLevel("Scene1");
			//manager.SwitchState(new PlayState(manager));
					
		}

	}
}

