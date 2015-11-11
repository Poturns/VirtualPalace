using System.Collections.Generic;
using BridgeApi.Controller;
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
        private CardboardHead cameraHead;
        private MessegeToaster toaster;

        public GameObject EventSystem { get { return eventSystem; } }
        public GazeInputModule SelectModule { get { return selectModule; } }
        public GazeCusor GCursor { get { return gazecusor; } }


        public AbstractGazeInputState(StateManager managerRef, string stateName) : base(managerRef, stateName)
        {
            Init();
        }

        public AbstractGazeInputState(AbstractGazeInputState otherStateInSameScene, string stateName) : this(otherStateInSameScene.Manager, stateName)
        {
            eventSystem = otherStateInSameScene.eventSystem;
            selectModule = otherStateInSameScene.selectModule;
            gazecusor = otherStateInSameScene.gazecusor;
            cameraHead = otherStateInSameScene.cameraHead;
            toaster = otherStateInSameScene.toaster;
        }

        /// <summary>
        /// EventSystem과 SelectModule을 초기화한다.
        /// </summary>
        protected virtual void Init()
        {
            FindEventSystemAndSelectModule();
        }

        /// <summary>
        /// GazeCursor의 SelectMode를 변경한다.
        /// </summary>
        /// <param name="selectMode">변경할 모드</param>
        protected void SetGazeInputMode(GAZE_MODE selectMode)
        {
            gazecusor.Mode = selectMode;
        }


        /// <summary>
        /// CameraHead를  고정한다.
        ///  </summary>
        protected void SetCameraLock(bool LockOn)
        {
            //cameraHead.ViewMoveOn = LockOn ? -1 : 0;
            if (LockOn)
                cameraHead.ViewMoveOn = -1;
            else
                cameraHead.ViewMoveOn = 0;
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
                    if (gazecusor == null)
                    {
                        Debug.LogError("Cannot find GazeCursor");
                    }
                }
                else
                    Debug.LogError("Cannot find EventSystem");
            }

            if (cameraHead == null)
                cameraHead = GameObject.Find("Head").GetComponent<CardboardHead>();

            if (toaster == null)
            {
                GameObject toasterGameobject = GameObject.Find("MessageToaster");
                if (toasterGameobject != null)
                    toaster = toasterGameobject.GetComponent<MessegeToaster>();
            }
        }

        public override void InputHandling(List<Operation> InputOp)
        {
            gazecusor.ResetGazingTime();
            base.InputHandling(InputOp);
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

        public override void ToastHandling(ToastMessage toast)
        {
            //base.ToastHandling(message);
            if (toaster == null)
                Debug.Log("!!!!!!!!!!!!!!!!!!!Toaster is Null!!!!!!!!!!!!!!!!!!!!!");
            else
            {
                toaster.CallMessageToaster(toast.Message);
            }
        }

    }
}
