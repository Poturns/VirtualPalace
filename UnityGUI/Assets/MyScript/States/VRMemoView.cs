using UnityEngine;
using MyScript.Interface;

namespace MyScript.States
{
	public class VRMemoView : IStateBase
	{
		private StateManager manager;
		
		private GameObject UIMemoBG;
		private GameObject UIMemoTxt;

		private GameObject TargetObj;
		
		public VRMemoView (StateManager managerRef , GameObject TargetObject)
		{
			manager = managerRef;
			TargetObj = TargetObject;
			
			Debug.Log ("MemoView");
			
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
		void Switch()
		{
			//Application.LoadLevel("Scene1");
			//manager.SwitchState(new PlayState(manager));
			
			
		}
	}
}

