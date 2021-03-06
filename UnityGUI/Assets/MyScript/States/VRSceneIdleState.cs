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

        public void SetAdditionalParameter(object param)
        {
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
            {
                Debug.Log("SaveLoaderFindFail");
                StateManager.SwitchScene(UnityScene.Lobby);
            }
            else
            {
                Saver.SavetoFile(() =>
                {
                    StateManager.SwitchScene(UnityScene.Lobby);
                });
            }

		}


    }
	
}

