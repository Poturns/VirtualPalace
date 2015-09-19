using UnityEngine;
using MyScript.Interface;

namespace MyScript.States
{
	public class BeginState : IStateBase
	{
		private StateManager manager;

		public BeginState (StateManager managerRef)
		{
			Debug.Log ("BeginState");
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

