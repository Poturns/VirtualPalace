using UnityEngine;
using MyScript.Interface;


namespace MyScript.States
{
	public class VRObjectViewExitState : IStateBase
	{
		private StateManager manager;
		
		private GameObject SelTarget;
		private GameObject SelText;
		
		public VRObjectViewExitState (StateManager managerRef)
		{
			manager = managerRef;
			
			
			Debug.Log ("VRObjectView");
			
			SelTarget = GameObject.FindWithTag ("Disposol");
			if (!SelTarget)
				Debug.Log ("VRObjectViewExitState:Sel Target is Null");
			GameObject.Destroy (SelTarget);

			SelText = GameObject.Find ("SelectTargetText");
			if (!SelText)
				Debug.Log ("Sel Text is Null");
			
			
			SelText.GetComponent<TextMesh>().text = "";
	

			
		}
		public void StateUpdate()
		{
			manager.SwitchState (new VRSceneIdleState (manager));
		}
		public void ShowIt()
		{
			
		}
		void Switch()
		{
			//Application.LoadLevel("Scene1");
			//manager.SwitchState(new PlayState(manager));
			
			
		}
	}
}

