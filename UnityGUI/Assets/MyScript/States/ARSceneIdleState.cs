using UnityEngine;
using MyScript.Interface;
using BridgeApi.Controller;
using System.Collections.Generic;
using BridgeApi.Controller.Request;

namespace MyScript.States
{

    public class ARSceneIdleState : AbstractGazeInputState, ISceneChangeState
    {
        private GameObject ARScreenObj;
        private ARScreen arScreen;

        private List<ARRenderingObject> ObjList;
        private GameObject ARObjPrefab;

        public ARSceneIdleState(StateManager managerRef) : base(managerRef, "ARSceneIdleState")
        {

        }

        public UnityScene UnitySceneID { get { return UnityScene.AR; } }

        public void OnSceneChanged()
        {
            Debug.Log("=============== " + Name + " : Scene changed");
            Init();
        }


        protected override void Init()
        {
            base.Init();
            // 스크린의 게임오브젝트를 가져온다
            if (ARScreenObj == null)
            {
                ARScreenObj = GameObject.Find("ARView");
                if (ARScreenObj == null && arScreen == null)
                {
                    arScreen = ARScreenObj.GetComponent<ARScreen>();
                }
            }
            ObjList = new List<ARRenderingObject>();


        }

        protected override void HandleCancelOperation()
        {
            ReturnToLobbyScene();
        }

        private void ReturnToLobbyScene()
        {
            arScreen.EndCamera();
            StateManager.SwitchScene(UnityScene.Lobby);
        }

        protected override void HandleSelectOperation()
        {
            //base.HandleSelectOperation();

            ARAddRequest.NewRequest().SendRequest(Manager, result => Debug.Log("===== ARAddRequest : " + result));
        }

        protected override void HandleOtherOperation(Operation operation)
        {
            //base.HandleOtherOperation(operation);
            switch (operation.Type)
            {
                case Operation.AR_RENDERING:
                    ARrenderItem item = JsonInterpreter.ParseARrenderItem(operation);
                    HandleARItemInput(item);
                    //Debug.Log("====== AR item : " + item);
                    break;

                default:
                    return;
            }

        }

        private void HandleARItemInput(ARrenderItem item)
        {
            //Update 리턴값이 false 면 생성
            if (!ARItemUpdate(item))
            {
                CreateARItem(item);
            }
        }

        //item을 받아서 List를 검사한뒤 있으면 업데이트하고 ture반환
        //없으면 false 반환
        private bool ARItemUpdate(ARrenderItem item)
        {
            if (ObjList == null || ObjList.Count == 0)
                return false;

            foreach (ARRenderingObject obj in ObjList)
            {
                if (obj.ARItem.resId == item.resId)
                {
                    obj.ARItem.screenX = item.screenX;
                    obj.ARItem.screenY = item.screenY;
                    obj.SetARPosition(item);
                    return true;
                }
            }

            return false;
        }
        private void CreateARItem(ARrenderItem item)
        {
            if (ARObjPrefab == null)
                ARObjPrefab = GameObject.Find("PrefabStore").GetComponent<ARPrefabload>().ARPrefab;

            GameObject NewObj = GameObject.Instantiate(ARObjPrefab) as GameObject;
            NewObj.transform.SetParent(ARScreenObj.transform.GetChild(0));
            ARRenderingObject NewAR = NewObj.GetComponent<ARRenderingObject>();
            NewAR.SetARItem(item);
            NewAR.SetARPosition(item);
            ObjList.Add(NewAR);
        }
    }
}

