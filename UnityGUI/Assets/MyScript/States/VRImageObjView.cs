using UnityEngine;
using MyScript.Interface;

namespace MyScript.States
{
	public class VRImageObjView : IStateBase
	{
		private StateManager manager;
		
		private GameObject ImageUI;
		private GameObject Target;

		public VRImageObjView (StateManager managerRef , GameObject TargetObject)
		{
			manager = managerRef;
			Target = TargetObject;
			
			Debug.Log ("VRImageObjectView");
			
			ImageUI = GameObject.Find ("ImageView");
			if (!ImageUI)
				Debug.Log ("ImageSelector is Null");

			
			ImageUI.GetComponent<MeshCollider> ().enabled = true;
		}
		public void StateUpdate()
		{
			ExitImageState ();
		}
		public void ShowIt()
		{
			
		}
		void ExitImageState()
		{
			Debug.Log("Exit Image");
			manager.SwitchState (new VRImageObjViewExit (manager, Target));

		}
	}
}

