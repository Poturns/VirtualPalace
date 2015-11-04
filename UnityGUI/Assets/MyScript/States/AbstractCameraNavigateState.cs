using BridgeApi.Controller;
using System.Collections.Generic;
using UnityEngine;

namespace MyScript.States
{
    /// <summary>
    /// Direction Operation을 처리해주는 IStateBase 클래스
    /// </summary>
    public class AbstractCameraNavigateState : AbstractGazeInputState
    {
        public GameObject Player { get { return player; } }

        private GameObject player;
        private CharacterController characterController;


        public AbstractCameraNavigateState(StateManager managerRef, string stateName) : base(managerRef, stateName)
        {
        }

        public AbstractCameraNavigateState(AbstractCameraNavigateState otherStateInSameScene, string stateName) : base(otherStateInSameScene.Manager, stateName)
        {
        }

        protected override void Init()
        {
            base.Init();
            InitPlayer();
        }

        protected void InitPlayer()
        {
            if (player == null)
                player = GameObject.Find("Player");
            characterController = player.GetComponent<CharacterController>();
        }

        protected override void HandleDirectionOperation(Dictionary<int, Direction> directionDictionary)
        {
            MoveCameraDirection(directionDictionary);
        }

        /// <summary>
        /// 주어진 Direction에 알맞게 이동한다.
        /// </summary>
        /// <param name="directionDictionary">이동할 방향이 기술된 Dictionary</param>
        protected virtual void MoveCameraDirection(Dictionary<int, Direction> directionDictionary)
        {
            InitPlayer();
            //string s = "";

            foreach (int key in directionDictionary.Keys)
            {
                //s += "dimension : " + key + ", Direction : " + DirList[key] + "\n";
                CameraNavigator.MoveCamera(characterController, directionDictionary[key]);
            }
            //Debug.Log("Direction Map :\n" + s);
        }

    }
}
