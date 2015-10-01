using UnityEngine;
using MyScript.Interface;
using AndroidApi.Controller;

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
		public void InputHandling(Operation[] InputOp)
		{
			foreach (Operation op in InputOp) 
			{
				if(op.Type == Operation.CANCEL)
				{
					
				}
				else if(op.Type == Operation.SELECT)
				{
					GameObject SelObj = EventSys.GetComponent<GazeInputModule>().RaycastedObj;
					if(!SelObj)
					{
						SelObj.GetComponent<IObject>().OnSelect();
					}
					//EventSystem.current.currentSelectedGameObject();
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

