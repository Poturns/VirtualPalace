using UnityEngine;
using System.Collections.Generic;
using MyScript.Interface;
using AndroidApi.Controller;
using AndroidApi.Controller.Request;

namespace MyScript.States
{
    public class VRMemoView : IStateBase
    {
        private StateManager manager;

        private GameObject UIMemoBG;
        private TextMesh UIMemoTxt;

        private MemoObject TargetObj;
        private GameObject EventSys;

        public VRMemoView(StateManager managerRef, GameObject TargetObject)
        {
            manager = managerRef;
            TargetObj = TargetObject.GetComponent<MemoObject>();

            Debug.Log("MemoView");

            EventSys = GameObject.Find("EventSystem");
            UIMemoBG = GameObject.Find("MemoView");
            if (!UIMemoBG)
                Debug.Log("Sel Target is Null");
            UIMemoBG.GetComponent<SpriteRenderer>().enabled = true;

            UIMemoTxt = GameObject.Find("MemoText").GetComponent<TextMesh>();
            if (!UIMemoTxt)
                Debug.Log("Sel Text is Null");

             UIMemoTxt.text = TargetObject.GetComponent<MemoObject>().GetMemo();
            //GameObject.Find ("Head").GetComponent<CardboardHead> ().ViewMoveOn = false;
        }
        public void StateUpdate()
        {
            if (Input.GetKeyUp(KeyCode.Q))
                manager.SwitchState(new VRMemoViewExit(manager, TargetObj.gameObject));
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
                        manager.SwitchState(new VRMemoViewExit(manager, TargetObj.gameObject));
                        break;

                    case Operation.SELECT:
                        new SpeechRequest().SendRequest((result) =>
                        {
                            Debug.Log(result);
                            switch (result.Status)
                            {
                                case RequestResult.STATUS_SUCCESS:
                                    TargetObj.InputMemo(result.Speech);
                                    manager.QueueOnMainThread(() => UIMemoTxt.text = TargetObj.GetMemo());
                                    break;

                                default:
                                    Debug.Log("SpeechRequest : failed!");
                                    break;
                            }
                        });
                        break;
                }
            }
        }
        void Switch()
        {
            //Application.LoadLevel("Scene1");
            //manager.SwitchState(new PlayState(manager));


        }
    }
}

