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

        public GameObject EventSystem { get { return eventSystem; } }
        public GazeInputModule SelectModule { get { return selectModule; } }

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
        /// GazeInputModule의 SelectMode를 변경한다.
        /// </summary>
        /// <param name="selectMode">변경할 모드</param>
        protected void SetGazeInputMode(int selectMode)
        {
            selectModule.Mode = selectMode;
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
                IRaycastedObject raycastedObject = SelObj.GetComponent<IRaycastedObject>();
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
