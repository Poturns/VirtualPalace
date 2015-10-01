using UnityEngine;
using MyScript.Interface;
using AndroidApi.Controller;
using UnityEngine.EventSystems;

namespace MyScript.States
{
	public class BeginState : IStateBase
	{
		private StateManager manager;
		private GameObject EventSys;

		public BeginState (StateManager managerRef)
		{
			Debug.Log ("BeginState");
			manager = managerRef;
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
						//SelObj.GetComponent<VR>().ClickObj();
					}
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

