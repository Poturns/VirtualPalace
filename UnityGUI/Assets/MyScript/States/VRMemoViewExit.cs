using UnityEngine;
using System.Collections.Generic;
using MyScript.Interface;
using BridgeApi.Controller;

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
            GameObject DisposolObj = GameObject.FindGameObjectWithTag("Disposol");
            if (DisposolObj) GameObject.Destroy(DisposolObj);

            UIMemoBG = GameObject.Find ("MemoView");
			if (!UIMemoBG)
				Debug.Log ("Sel Target is Null");
			UIMemoBG.GetComponent<SpriteRenderer> ().enabled = false;
			
			UIMemoTxt = GameObject.Find ("MemoText");
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

