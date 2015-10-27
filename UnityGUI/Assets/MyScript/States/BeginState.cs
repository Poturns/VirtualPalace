using UnityEngine;
using System.Collections.Generic;
using MyScript.Interface;
using BridgeApi.Controller;
using System;

namespace MyScript.States
{
    public class BeginState : ISceneChangeState
	{
		private StateManager manager;
		private GameObject EventSys;
		private GazeInputModule SelectModule;

		public BeginState (StateManager managerRef)
		{
            manager = managerRef;
			OnSceneChanged ();
        }


        public void OnSceneChanged()
        {
            Debug.Log("BeginState");
            EventSys = GameObject.Find("EventSystem");

            SelectModule = EventSys.GetComponent<GazeInputModule>();
			SelectModule.Mode = 0;
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
                switch (op.Type) {
                    case Operation.CANCEL:

                        break;

                    case Operation.SELECT:
                        
                        Debug.Log("SelectModule.RaycastedGameObject : " + SelectModule.RaycastedGameObject);
                       
                        if (SelectModule == null)
                        {
                            if (EventSys == null)
                            {
                                EventSys = GameObject.Find("EventSystem");
                            }
                            SelectModule = EventSys.GetComponent<GazeInputModule>();
                        }
                        
                        
                        if(SelectModule.RaycastedGameObject != null)
                        {
                            IRaycastedObject obj = SelectModule.RaycastedGameObject.GetComponent<IRaycastedObject>();

                            Debug.Log("obj : " + obj);
                            if (obj != null) {
                                obj.OnSelect();
                            }
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

