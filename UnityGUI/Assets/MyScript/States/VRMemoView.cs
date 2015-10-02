using UnityEngine;
using System.Collections.Generic;
using MyScript.Interface;
using AndroidApi.Controller;

namespace MyScript.States
{
	public class VRMemoView : IStateBase
	{
		private StateManager manager;
		
		private GameObject UIMemoBG;
		private GameObject UIMemoTxt;

		private GameObject TargetObj;
		private GameObject EventSys;
		
		public VRMemoView (StateManager managerRef , GameObject TargetObject)
		{
			manager = managerRef;
			TargetObj = TargetObject;
			
			Debug.Log ("MemoView");

			EventSys = GameObject.Find ("EventSystem");
			UIMemoBG = GameObject.Find ("MemoView");
			if (!UIMemoBG)
				Debug.Log ("Sel Target is Null");
			UIMemoBG.GetComponent<SpriteRenderer> ().enabled = true;
			
			UIMemoTxt = GameObject.Find ("MemoText");
			if (!UIMemoTxt)
				Debug.Log ("Sel Text is Null");
			
			//TargetObject.GetComponent<MemoObject>().MemoPrefab;
			TextMesh T = UIMemoTxt.GetComponent<TextMesh> ();
			T.text = "New Memo Test";
			//GameObject.Find ("Head").GetComponent<CardboardHead> ().ViewMoveOn = false;
		}
		public void StateUpdate()
		{
			if (Input.GetKeyUp (KeyCode.Q))
				manager.SwitchState(new VRMemoViewExit(manager,TargetObj));
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
					manager.SwitchState(new VRMemoViewExit(manager,TargetObj));
					break;
					
				case Operation.SELECT:
					
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

