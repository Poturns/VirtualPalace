using UnityEngine;
using MyScript.Interface;

namespace MyScript.States
{
	public class VRObjectView : IStateBase
	{
		private StateManager manager;

		private GameObject SelTarget;
		private GameObject SelText;
		
		public VRObjectView (StateManager managerRef , GameObject TargetObject)
		{
			manager = managerRef;


			Debug.Log ("VRObjectView");

			SelTarget = GameObject.Find ("SelectTargetPos");
			if (!SelTarget)
				Debug.Log ("Sel Target is Null");
			SelText = GameObject.Find ("SelectTargetText");
			if (!SelText)
				Debug.Log ("Sel Text is Null");


			GameObject Temp = GameObject.Instantiate (TargetObject.GetComponent<MemoObject>().UIObject, SelTarget.transform.position, SelTarget.transform.rotation) as GameObject;

			Temp.tag = "Disposol";
			Temp.transform.SetParent (SelTarget.transform);

			//TargetObject.GetComponent<MemoObject>().MemoPrefab;
			string NewTxt = TargetObject.GetComponent<MemoObject>().GetMemo();
			TextMesh T = SelText.GetComponent<TextMesh> ();
			T.text = NewTxt;
			//GameObject.Find ("Head").GetComponent<CardboardHead> ().ViewMoveOn = false;
		}
		public void StateUpdate()
		{
			
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

