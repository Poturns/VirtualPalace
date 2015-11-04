using UnityEngine;
using MyScript.Interface;

namespace MyScript.States
{
	public class VRSceneIdleState : AbstractCameraNavigateState, ISceneChangeState
	{
		public VRSceneIdleState(StateManager managerRef) : base(managerRef, "VRSceneState")
		{
		}
		
		public void OnSceneChanged()
		{
			Debug.Log(Name + " : Scene changed");
			Init();
		}
		public override void StateUpdate()
		{
			if (Input.GetKeyUp (KeyCode.K))
				ReturnToMainScene ();
			if (Input.GetKeyUp (KeyCode.J))
				TestCode ();
		}
		//Load 테스트 코드 << 내가 삭제 까먹으면 삭제해도됨
		private void TestCode()
		{
			SaveLoader Saver = GameObject.Find ("_Script").GetComponent<SaveLoader> ();
			if (Saver == null)
				Debug.Log ("SaveLoaderFindFail");
			else
				Saver.LoadToFile ();
		}
		protected override void Init()
		{
			base.Init();
			GameObject DisposolObj = GameObject.FindGameObjectWithTag("Disposol");
			if (DisposolObj != null) GameObject.Destroy(DisposolObj);
			
			SetGazeInputMode(0);
		}
		
		void Switch()
		{
			//Application.LoadLevel("Scene1");
			//manager.SwitchState(new PlayState(manager));
		}
		
		protected override void HandleCancelOperation()
		{
			ReturnToMainScene();
		}
		
		
		private void ReturnToMainScene()
		{
			SaveLoader Saver = GameObject.Find ("_Script").GetComponent<SaveLoader> ();
			if (Saver == null)
				Debug.Log ("SaveLoaderFindFail");
			else
				Saver.SavetoFile ();
			StateManager.SwitchScene(StateManager.SCENE_MAIN);
		}
		
	}
	
}

