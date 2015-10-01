using UnityEngine;
using MyScript.Interface;
using AndroidApi.Controller;
using System.Collections.Generic;

namespace MyScript.States
{
	public class VRObjectViewExitState : IStateBase
	{
		private StateManager manager;
		
		private GameObject UIBookMesh;
		private GameObject UITitleTextObj;
		
		public VRObjectViewExitState (StateManager managerRef)
		{
			manager = managerRef;
			
			
			Debug.Log ("VRObjectViewExit");
			
			UIBookMesh = GameObject.Find ("UIBook");
			if (!UIBookMesh)
				Debug.Log ("Sel Target is Null");

			UIBookMesh.GetComponent<MeshRenderer> ().enabled = false;

			UITitleTextObj = GameObject.Find ("UITitleText");
			if (!UITitleTextObj)
				Debug.Log ("Sel Text is Null");
			
			UITitleTextObj.GetComponent<TextMesh>().text = "";
	

			
		}
		public void StateUpdate()
		{
			manager.SwitchState (new VRSceneIdleState (manager));
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

