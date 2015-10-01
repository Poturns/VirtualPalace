using UnityEngine;
using MyScript.Interface;
using AndroidApi.Controller;
using UnityEngine.EventSystems;


namespace MyScript.States
{
	public class VRSceneIdleState : IStateBase
	{
		private StateManager manager;
		private GameObject EventSys;

		public VRSceneIdleState (StateManager managerRef)
		{
			Debug.Log ("VRSceneState");
			manager = managerRef;
			EventSys = GameObject.Find ("EventSystem");
			if(!EventSys) Debug.Log("Event System Find Fail");
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
						SelObj.GetComponent<MemoObject>().ClickObj();
					}
					//EventSystem.current.currentSelectedGameObject();
				}
			}
		}
		void Switch()
		{
			//Application.LoadLevel("Scene1");
			//manager.SwitchState(new PlayState(manager));
			
			
		}
	}
}

