using BridgeApi.Controller;
using MyScript.Interface;
using System;
using System.Collections.Generic;
using UnityEngine;

namespace MyScript.States
{
    /// <summary>
    /// Input을 분류해주는 IStateBase 클래스
    /// </summary>
    public abstract class AbstractInputHandleState : IStateBase
    {
        private StateManager manager;
        private readonly string stateName;
        public string Name { get { return stateName; } }

        protected internal StateManager Manager { get { return manager; } }

        public const string DESTROY_MARK = "Disposol";

        public AbstractInputHandleState(StateManager managerRef, string stateName)
        {
            manager = managerRef;
            this.stateName = stateName;
            Debug.Log("=============== " + stateName);
        }

        /// <summary>
        /// DESTROY_MARK (Disposol) tag가 붙은 GameObject를 destory한다.
        /// </summary>
        protected internal static void DestroyMarkedObject()
        {
            GameObject DisposolObj = GameObject.FindGameObjectWithTag(DESTROY_MARK);
            if (DisposolObj != null) UnityEngine.Object.Destroy(DisposolObj);
        }

        /// <summary>
        /// 주어진 state로 변경한다.
        /// </summary>
        /// <param name="newState">변경될 state</param>
        protected internal void SwitchState(IStateBase newState)
        {
            manager.SwitchState(newState);
        }

        /// <summary>
        /// 작업을 Unity의 MainThread에서 수행한다.<para/>
        /// </summary>
        /// <param name="a">Unity MainThread에서 실행시킬 작업</param>
        /// <param name="runImmediatelyIfMainThread">이 메소드를 호출한 Thread가 MainThread이면 즉각적으로 실행시킬지 여부</param>
        public void QueueOnMainThread(Action a, bool runImmediatelyIfMainThread)
        {
            manager.QueueOnMainThread(a, runImmediatelyIfMainThread);
        }

        /// <summary>
        /// 작업을 Unity의 MainThread에서 수행한다.<para/>
        /// 이 메소드를 호출한 Thread가 MainThread라면 그대로 실행시킨다.
        /// </summary>
        /// <param name="a">Unity MainThread에서 실행시킬 작업</param>
        public void QueueOnMainThread(Action a)
        {
            manager.QueueOnMainThread(a);
        }


        public virtual void StateUpdate()
        {
        }

        public virtual void ShowIt()
        {
        }

        public virtual void InputHandling(List<Operation> InputOp)
        {
            foreach (Operation operation in InputOp)
            {
                switch (operation.Type)
                {
                    case Operation.CANCEL:
                        HandleCancelOperation();
                        break;

                    case Operation.SELECT:
                        HandleSelectOperation();
                        break;

                    case Operation.DEEP:
                        HandleDeepOperation();
                        break;

                    default:
                        if (operation.IsDirection)
                            HandleDirectionOperation(JsonInterpreter.ParseDirectionAmount(operation));
                        else
                            HandleOtherOperation(operation);
                        break;
                }
            }
        }

        /// <summary>
        /// Cancel 명령을 처리한다.
        /// </summary>
        protected virtual void HandleCancelOperation() { }


        /// <summary>
        /// Deep 명령을 처리한다.
        /// </summary>
        protected virtual void HandleDeepOperation() { }


        /// <summary>
        /// Select 명령을 처리한다.
        /// </summary>
        protected virtual void HandleSelectOperation() { }

        /// <summary>
        /// Direction 명령을 처리한다.
        /// </summary>
        /// <param name="directionDictionary">Operation에서 해석된 Direction의 List</param>
        protected virtual void HandleDirectionOperation(Dictionary<int, Direction> directionDictionary) { }

        /// <summary>
        /// Cancel, Select, Direction 이외의 다른 명령을 처리한다.
        /// </summary>
        /// <param name="operation">Cancel, Select, Direction, Deep 이외의 다른 명령</param>
        protected virtual void HandleOtherOperation(Operation operation) { }
    }

}
