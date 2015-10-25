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
		private UITransform UITrans;
		public VRModelSelect (StateManager managerRef , GameObject TargetObject)
		{
			manager = managerRef;
			Target = TargetObject;
			GameObject UI = GameObject.Find ("ObjModelSelectUI");
			for (int i = 0; i < UI.transform.childCount; i++) 
			{
				UI.transform.GetChild(i).gameObject.SetActive(true);
				
			}
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
				if(op.Type == Operation.CANCEL)
				{
					
				}
				else if(op.Type == Operation.SELECT)
				{
					
					
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

