using UnityEngine;
using System.Collections.Generic;
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
			EventSys = GameObject.Find ("EventSystem");
			manager = managerRef;
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
					GameObject SelObj = EventSys.GetComponent<GazeInputModule>().RaycastedObj;
					if(!SelObj)
					{
						SelObj.GetComponent<IObject>().OnSelect();
					}
					break;
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

