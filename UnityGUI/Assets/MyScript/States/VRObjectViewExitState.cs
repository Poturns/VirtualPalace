using UnityEngine;

namespace MyScript.States
{
    public class VRObjectViewExitState : AbstractGazeInputState
	{
		private GameObject UIBookMesh;
		private GameObject UITitleTextObj;
		
		public VRObjectViewExitState (StateManager managerRef) : base(managerRef, "VRObjectViewExit")
		{
			
			
            GameObject DisposolObj = GameObject.FindGameObjectWithTag("Disposol");
            if (DisposolObj) GameObject.Destroy(DisposolObj);
            UIBookMesh = GameObject.Find ("UIBook");
			if (!UIBookMesh)
				Debug.Log ("Sel Target is Null");
		
			//UIBookMesh.GetComponent<MeshRenderer> ().enabled = false;


			UITitleTextObj = GameObject.Find ("UITitleText");
			if (!UITitleTextObj)
				Debug.Log ("Sel Text is Null");
			
			UITitleTextObj.GetComponent<TextMesh>().text = "";

		}

		public override void StateUpdate()
		{
			SwitchState (new VRSceneIdleState (Manager));
			SetGazeInputMode (0);
			SetCameraLock (false);
		}

		
		
		void Switch()
		{
			//Application.LoadLevel("Scene1");
			//manager.SwitchState(new PlayState(manager));
					
		}

	}

}

