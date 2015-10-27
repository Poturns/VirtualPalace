using UnityEngine;
using MyScript.Interface;
using System.Collections.Generic;
using BridgeApi.Controller;

namespace MyScript.States
{
	public class VRModelSelect : IStateBase
	{
		private StateManager manager;
		
		//BookCaseTrigger
		private GameObject Target;
		private GameObject EventSys;
		private GazeInputModule SelectModule;

		private UITransform UITrans;
		public VRModelSelect (StateManager managerRef , GameObject TargetObject)
		{
			manager = managerRef;
			Target = TargetObject;

			EventSys = GameObject.Find("EventSystem");
			if (EventSys == null) Debug.Log("Event System Find Fail");
			
			SelectModule = EventSys.GetComponent<GazeInputModule>();
			if (SelectModule == null) Debug.Log("GazeInputModule == null");
			SelectModule.Mode = 1;

			GameObject UI = GameObject.Find ("ModelSelectUI");
			UI.GetComponent<UITransform> ().OnOffOUIButton (true);

			UITrans = UI.GetComponent<UITransform> (); 
			UITrans.LockCameraRot ();
			Debug.Log ("VRModelSelect");
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
					break;
					
				case Operation.SELECT:
					//Debug.Log(SelectModule);
					GameObject SelObj = SelectModule.RaycastedGameObject;
					if (SelObj != null) 
					{
						// Debug.Log("SelObj -> " + SelObj.name);
						
						IRaycastedObject raycastedObject = SelObj.GetComponent<IRaycastedObject> ();
						if (raycastedObject != null) 
						{
							//Debug.Log("IRaycastedObject != null");
							raycastedObject.OnSelect ();
						
						}
						//else                                Debug.Log("IRaycastedObject == null");
					}
					/* else
                         {
                             Debug.Log("SelObj == null");
                         }
                         */
					//EventSystem.current.currentSelectedGameObject();
					break;
				}
			}
		}
		public void EndState()
		{
			UITrans.UnlockCameraRot ();
			GameObject UI = UITrans.gameObject;
			for (int i = 0; i < UI.transform.childCount; i++) 
			{
				UI.transform.GetChild(i).gameObject.SetActive(false);
			}
			
		}
		
	}
}

