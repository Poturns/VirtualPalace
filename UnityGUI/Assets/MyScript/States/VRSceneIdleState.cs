using UnityEngine;
using MyScript.Interface;
using AndroidApi.Controller;
using UnityEngine.EventSystems;
using System.Collections.Generic;

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
		public void InputHandling(List<Operation> InputOp)
		{
			foreach (Operation op in InputOp) 
			{
                switch (op.Type)
                {
                    case Operation.CANCEL:
                        StateManager.SwitchScene(StateManager.SCENE_MAIN);
                        break;

                    case Operation.SELECT:
                        GameObject SelObj = EventSys.GetComponent<GazeInputModule>().RaycastedGameObject;
                        if (SelObj != null)
                        {
                            SelObj.GetComponent<IRaycastedObject>().OnSelect();
                        }
                        //EventSystem.current.currentSelectedGameObject();
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

