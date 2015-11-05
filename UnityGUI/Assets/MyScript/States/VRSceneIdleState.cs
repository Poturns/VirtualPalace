using UnityEngine;
using MyScript.Interface;

namespace MyScript.States
{
    public class VRSceneIdleState : AbstractCameraNavigateState, ISceneChangeState
	{
        public static VRSceneIdleState CopyFromCurrentState(AbstractGazeInputState otherStateInVRScene)
        {
            return new VRSceneIdleState(otherStateInVRScene);
        }

        public VRSceneIdleState(StateManager managerRef) : base(managerRef, "VRSceneState")
		{
		}

        public VRSceneIdleState(AbstractGazeInputState otherStateInVRScene) : base(otherStateInVRScene.Manager, "VRSceneState")
        {
        }


        public UnityScene UnitySceneID { get { return UnityScene.VR; } }

        public void OnSceneChanged()
		{
			Debug.Log("=============== " + Name + " : Scene changed");
			Init();
		}
		public override void StateUpdate()
		{
			if (Input.GetKeyUp (KeyCode.K))
				ReturnToLobbyScene ();
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
            DestroyMarkedObject();
			
			SetGazeInputMode(GAZE_MODE.OBJECT);
            SetCameraLock(false);


		}

        protected override void HandleCancelOperation()
		{
			ReturnToLobbyScene();
		}
		
		
		private void ReturnToLobbyScene()
		{

			SaveLoader Saver = GameObject.Find ("_Script").GetComponent<SaveLoader> ();
			if (Saver == null)
				Debug.Log ("SaveLoaderFindFail");
			else
				Saver.SavetoFile ();
			StateManager.SwitchScene(UnityScene.Lobby);

		}


    }
	
}

