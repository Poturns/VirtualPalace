using UnityEngine;
using MyScript.Interface;

namespace MyScript.States
{
	public class VRSceneIdleState : IStateBase
	{
		private StateManager manager;


		public VRSceneIdleState (StateManager managerRef)
		{
			Debug.Log ("VRSceneState");
			manager = managerRef;
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

