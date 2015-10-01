using UnityEngine;
using System.Collections.Generic;
using MyScript.Interface;
using AndroidApi.Controller;

namespace MyScript.States
{
	public class VRImageObjView : IStateBase
	{
		private StateManager manager;
		private GameObject EventSys;
		private GameObject ImageUI;
		private GameObject Target;

		public VRImageObjView (StateManager managerRef , GameObject TargetObject)
		{
			manager = managerRef;
			Target = TargetObject;
			
			Debug.Log ("VRImageObjectView");
			EventSys = GameObject.Find ("EventSystem");
			ImageUI = GameObject.Find ("ImageView");
			if (!ImageUI)
				Debug.Log ("ImageSelector is Null");

			
			ImageUI.GetComponent<MeshCollider> ().enabled = true;
		}
		public void StateUpdate()
		{

		}
		public void ShowIt()
		{
			
		}
		public void InputHandling(List<Operation> InputOp)
		{
			foreach (Operation op in InputOp) 
			{
				switch(op.Type){
				case Operation.CANCEL:

					break;

				case Operation.SELECT:
						GameObject SelObj = EventSys.GetComponent<GazeInputModule>().RaycastedGameObject;
						if(!SelObj)
						{
							SelObj.GetComponent<IRaycastedObject>().OnSelect();
						}
						//EventSystem.current.currentSelectedGameObject();
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

