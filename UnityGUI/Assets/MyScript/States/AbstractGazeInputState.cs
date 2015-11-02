using MyScript;
using MyScript.Interface;
using UnityEngine;

namespace MyScript.States
{
    /// <summary>
    /// EventSystem과 GazeInputModule을 가진 StateBase 클래스
    /// </summary>
    public abstract class AbstractGazeInputState : AbstractInputHandleState
    {
        private GameObject eventSystem;
        private GazeInputModule selectModule;
		private GazeCusor gazecusor;
        public GameObject EventSystem { get { return eventSystem; } }
        public GazeInputModule SelectModule { get { return selectModule; } }
		public GazeCusor GCursor {get {return gazecusor;}}

        public AbstractGazeInputState(StateManager managerRef, string stateName) : base(managerRef, stateName)
        {
            Init();
        }

        /// <summary>
        /// EventSystem과 SelectModule을 초기화한다.
        /// </summary>
        protected virtual void Init()
        {
            FindEventSystemAndSelectModule();
        }

        /// <summary>
        /// GazeCurosr의 SelectMode를 변경한다.
        /// </summary>
        /// <param name="selectMode">변경할 모드</param>
		/// Mode 0 : Interactive 1 : 2dMoveUI 2: Disable Cursor
        protected void SetGazeInputMode(GAZE_MODE selectMode)
        {
			gazecusor.Mode = selectMode;
        }


		// <summary>
		/// CameraHead를  고정한다.
		///  </summary>
		protected void SetCameraLock(bool LockOn)
		{
			CardboardHead TempHead = GameObject.Find ("Head").GetComponent<CardboardHead> ();
			if (LockOn)
				TempHead.ViewMoveOn = -1;
			else
				TempHead.ViewMoveOn = 0;
		}

		/// <summary>
        /// EventSystem과 SelectModule을 찾아서 할당한다.
        /// </summary>
        protected virtual void FindEventSystemAndSelectModule()
        {
            if (selectModule == null)
            {
                if (eventSystem == null)
                {
                    eventSystem = GameObject.Find("EventSystem");
                }
                if (eventSystem != null)
                {
                    selectModule = eventSystem.GetComponent<GazeInputModule>();
					if (selectModule == null)
                    {
                        Debug.LogError("Cannot find GazeInputModule");
                    }
					gazecusor = selectModule.cursor.GetComponent<GazeCusor>();
					if(gazecusor == null)
					{
						Debug.LogError("Cannot find GazeCursor");
					}
                }
                else
                    Debug.LogError("Cannot find EventSystem");
            }

        }

        protected override void HandleSelectOperation()
        {
            FindEventSystemAndSelectModule();

            GameObject SelObj = selectModule.RaycastedGameObject;
            if (SelObj != null)
            {
				AbstractBasicObject raycastedObject = SelObj.GetComponent<AbstractBasicObject>();
                if (raycastedObject != null)
                {
                    raycastedObject.OnSelect();
                }
                else
                {
                    Debug.Log("raycastedObject == null");
                }
            }
            else
            {
                Debug.Log("Select Object == null");
            }
        }


    }
}
