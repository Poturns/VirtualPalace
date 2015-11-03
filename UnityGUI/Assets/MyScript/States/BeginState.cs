using UnityEngine;
using MyScript.Interface;

namespace MyScript.States
{
    public class BeginState : AbstractCameraNavigateState, ISceneChangeState
    {
        public BeginState(StateManager managerRef) : base(managerRef, "BeginState")
        {
        }

        public UnityScene UnitySceneID { get { return UnityScene.Lobby; } }

        public void OnSceneChanged()
        {
            Debug.Log("=============== " + Name + " : Scene changed");
            Init();
        }

        protected override void Init()
        {
            base.Init();
            SetGazeInputMode(GAZE_MODE.OBJECT);
        }

    }
}

