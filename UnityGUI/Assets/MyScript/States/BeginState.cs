using UnityEngine;
using MyScript.Interface;

namespace MyScript.States
{
    public class BeginState : AbstractCameraNavigateState, ISceneChangeState
    {
		public BeginState (StateManager managerRef) : base(managerRef, "BeginState")
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
            SetGazeInputMode(0);
        }

        void Switch()
		{
			//Application.LoadLevel("Scene1");
			//manager.SwitchState(new PlayState(manager));
		}

    }
}

