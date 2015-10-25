using UnityEngine;
using MyScript.Interface;
using System.Collections.Generic;
using BridgeApi.Controller;

namespace MyScript.States
{
    public class VRSceneIdleState : ISceneChangeState
    {
        private StateManager manager;
        GameObject EventSys;
        GazeInputModule SelectModule;
        GameObject Player;
        public VRSceneIdleState(StateManager managerRef)
        {
            manager = managerRef;
        }

        public void OnSceneChanged()
        {
            Debug.Log("VRSceneState");
            GameObject DisposolObj = GameObject.FindGameObjectWithTag("Disposol");
            if (DisposolObj) GameObject.Destroy(DisposolObj);

            EventSys = GameObject.Find("EventSystem");
            if (EventSys == null) Debug.Log("Event System Find Fail");
            //else Debug.Log(EventSys);

            SelectModule = EventSys.GetComponent<GazeInputModule>();
            if (SelectModule == null) Debug.Log("GazeInputModule == null");
            Player = GameObject.Find("Player");
            //else Debug.Log(SelectModule);
        }

        public void StateUpdate()
        {
            if (Input.GetKeyUp(KeyCode.Q))
                ReturnToMainScene();
        }

        public void ShowIt()
        {

        }

        public void InputHandling(List<Operation> InputOp)
        {
            foreach (Operation op in InputOp)
            {
                switch (op.Type)
                {
                    case Operation.CANCEL:
                        ReturnToMainScene();
                        break;

                    case Operation.SELECT:
                        /*
                        if (SelectModule == null)
                        {
                            // Debug.Log("1. Select Module == null");
                            if (EventSys == null)
                            {
                                //Debug.Log("2. EventSys == null");
                                EventSys = GameObject.Find("EventSystem");
                            }
                            SelectModule = EventSys.GetComponent<GazeInputModule>();
                        }
                        */
                        //Debug.Log(SelectModule);

                        GameObject SelObj = SelectModule.RaycastedGameObject;
                        if (SelObj != null)
                        {
                            // Debug.Log("SelObj -> " + SelObj.name);

                            IRaycastedObject raycastedObject = SelObj.GetComponent<IRaycastedObject>();
                            if (raycastedObject != null)
                            {
                                //Debug.Log("IRaycastedObject != null");
                                raycastedObject.OnSelect();
                            }
                            //else                                Debug.Log("IRaycastedObject == null");
                        }
                        /* else
                         {
                             Debug.Log("SelObj == null");
                         }
                         */
                        //EventSystem.current.currentSelectedGameObject();
                        break;
                    default:
                        if (op.IsDirection())
                        {
                            MoveDir(op);
                        }
                        break;
                }

            }
        }

        void MoveDir(Operation op)
        {
            Dictionary<int, Direction> DirList = JsonInterpreter.ParseDirectionAmount(op);
            if (DirList.ContainsKey(Direction.DIMENSION_2))
                MoveCamera(DirList[Direction.DIMENSION_2]);
        }

        void Switch()
        {
            //Application.LoadLevel("Scene1");
            //manager.SwitchState(new PlayState(manager));


        }

        void MoveCamera(Direction direction)
        {

            Vector3 NewVector;

            switch (direction.Value)
            {
                case Direction.RIGHT:
                    NewVector = Vector3.right;
                    break;
                case Direction.LEFT:
                    NewVector = -Vector3.right;
                    break;
                case Direction.DOWN:
                    NewVector = -Vector3.forward;
                    break;
                case Direction.UP:
                    NewVector = Vector3.forward;
                    break;
                default:
                    NewVector = new Vector3(0, 0, 0);
                    break;
            }
            Player.transform.position += NewVector;
        }

        private void ReturnToMainScene()
        {
            StateManager.SwitchScene(StateManager.SCENE_MAIN);
        }

    }

}

