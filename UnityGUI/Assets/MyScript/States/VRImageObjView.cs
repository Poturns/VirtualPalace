using UnityEngine;
using System.Collections.Generic;
using MyScript.Interface;
using AndroidApi.Controller;

namespace MyScript.States
{
	public class VRImageObjView : IStateBase
	{
		private StateManager manager;
		private GazeInputModule SelectModule;
		private GameObject ImageUI;
		private GameObject Target;

		public VRImageObjView (StateManager managerRef , GameObject TargetObject)
		{
			manager = managerRef;
			Target = TargetObject;

			GameObject DisposolObj = GameObject.FindGameObjectWithTag ("Disposol");
			if(DisposolObj)GameObject.Destroy (DisposolObj);

			Debug.Log ("VRImageObjectView");
			SelectModule = GameObject.Find ("EventSystem").GetComponent<GazeInputModule>();
			ImageUI = GameObject.Find ("ImageView");
			if (!ImageUI)
				Debug.Log ("ImageSelector is Null");

			
			ImageUI.GetComponent<MeshCollider> ().enabled = true;
			ImageUI.GetComponent<MeshRenderer> ().enabled = true;
		}
		public void StateUpdate()
		{

		}
		public void ShowIt()
		{
			
		}
		public void InputHandling(List<Operation> InputOp)
		{
			foreach (Operation op in InputOp) {
				switch (op.Type) {
				case Operation.CANCEL:
					ExitImageState();

					break;
					
				case Operation.SELECT:
					if (SelectModule.RaycastedGameObject != null) {
						IRaycastedObject obj = SelectModule.RaycastedGameObject.GetComponent<IRaycastedObject> ();
						
						if (obj != null) {
							obj.OnSelect ();
						}
					}
					
					break;
				}
			}
		}
	
		void ExitImageState()
		{
			Debug.Log("Exit Image");
			manager.SwitchState (new VRImageObjViewExit (manager, Target));

		}
	}
}

