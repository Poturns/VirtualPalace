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
            StateManager.SwitchScene(StateManager.SCENE_MAIN);
        }

    }

}

