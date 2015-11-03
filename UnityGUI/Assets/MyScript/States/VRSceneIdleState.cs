using UnityEngine;
using MyScript.Interface;

namespace MyScript.States
{
    public class VRSceneIdleState : AbstractCameraNavigateState, ISceneChangeState
	{
		public VRSceneIdleState(StateManager managerRef) : base(managerRef, "VRSceneState")
		{
		}

        public UnityScene UnitySceneID { get { return UnityScene.VR; } }

        public void OnSceneChanged()
		{
			Debug.Log("=============== " + Name + " : Scene changed");
			Init();
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
			StateManager.SwitchScene(UnityScene.Lobby);
		}


    }
	
}

