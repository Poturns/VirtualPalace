using UnityEngine;
using System.Collections.Generic;
using MyScript.Interface;
using AndroidApi.Controller;

namespace MyScript.States
{
	public class VRMemoViewExit : IStateBase
	{
		private StateManager manager;
		
		private GameObject UIMemoBG;
		private GameObject UIMemoTxt;

		public VRMemoViewExit (StateManager managerRef, GameObject TargetObject)
		{
			manager = managerRef;
			
			
			Debug.Log ("VRMemoView");
			
			UIMemoBG = GameObject.Find ("UIBook");
			if (!UIMemoBG)
				Debug.Log ("Sel Target is Null");
			UIMemoBG.GetComponent<SpriteRenderer> ().enabled = false;
			
			UIMemoTxt = GameObject.Find ("UITitleText");
			if (!UIMemoTxt)
				Debug.Log ("Sel Text is Null");
			
			//TargetObject.GetComponent<MemoObject>().MemoPrefab;
			TextMesh T = UIMemoTxt.GetComponent<TextMesh> ();
			T.text = "";
			//GameObject.Find ("Head").GetComponent<CardboardHead> ().ViewMoveOn = false;

			manager.SwitchState (new VRObjectView (managerRef, TargetObject));
			
		}
		public void StateUpdate()
		{

		}
		public void ShowIt()
		{
			
		}

		public void InputHandling(List<Operation> InputOp)
		{
			
		}
		void Switch()
		{
			//Application.LoadLevel("Scene1");
			//manager.SwitchState(new PlayState(manager));
			
			
		}
	}
}

