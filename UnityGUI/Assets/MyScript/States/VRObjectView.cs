using UnityEngine;
using MyScript.Interface;
using System.Collections.Generic;
using AndroidApi.Controller;


namespace MyScript.States
{
	public class VRObjectView : IStateBase
	{
		private StateManager manager;

		private GameObject UIBookMesh;
		private GameObject UITitleTextObj;

		private GameObject Target;
		private GameObject EventSys;

		public VRObjectView (StateManager managerRef , GameObject TargetObject)
		{
			manager = managerRef;
			Target = TargetObject;

			Debug.Log ("VRObjectView");

			EventSys = GameObject.Find ("EventSystem");
			UIBookMesh = GameObject.Find ("UIBook");
			if (!UIBookMesh)
				Debug.Log ("Sel Target is Null");
			UIBookMesh.GetComponent<MeshRenderer> ().enabled = true;

			UITitleTextObj = GameObject.Find ("UITitleText");
			if (!UITitleTextObj)
				Debug.Log ("Sel Text is Null");

			//TargetObject.GetComponent<MemoObject>().MemoPrefab;
			string NewTxt = TargetObject.GetComponent<MemoObject>().Title;
			TextMesh T = UITitleTextObj.GetComponent<TextMesh> ();
			T.text = NewTxt;
			//GameObject.Find ("Head").GetComponent<CardboardHead> ().ViewMoveOn = false;
		}
		public void StateUpdate()
		{

				Debug.Log ("A");
			// Input
				
			//ChangeMemoScene ();

		
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
					UIBookMesh.GetComponent<MeshRenderer> ().enabled = false;
					UITitleTextObj.GetComponent<TextMesh> ().text = "";
					manager.SwitchState (new VRSceneIdleState(manager));
				}
				else if(op.Type == Operation.SELECT)
				{
					
					ChangeMemoScene ();
				}
			}
		}
		void ChangeMemoScene()
		{
			UIBookMesh.GetComponent<MeshRenderer> ().enabled = false;
			UITitleTextObj.GetComponent<TextMesh> ().text = "";
			manager.SwitchState (new VRMemoView (manager, Target));

		}
	}
}

