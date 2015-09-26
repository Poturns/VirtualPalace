using UnityEngine;
using MyScript.Interface;

namespace MyScript.States
{
	public class VRImageObjViewExit : IStateBase
	{
		private StateManager manager;
		
		private GameObject ImageUI;
		
		
		public VRImageObjViewExit (StateManager managerRef , GameObject TargetObject)
		{
			manager = managerRef;
			
			
			Debug.Log ("VRImageObjectViewEXIT");
			
			ImageUI = GameObject.Find ("ImageView");
			if (!ImageUI)
				Debug.Log ("ImageSelector is Null");
			
			
			ImageUI.GetComponent<MeshCollider> ().enabled = false;
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

