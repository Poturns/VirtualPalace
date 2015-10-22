using UnityEngine;
using System.Collections.Generic;
using MyScript.Interface;
using BridgeApi.Controller;
using BridgeApi.Controller.Request;

namespace MyScript.States
{

    public class VRMemoView : IStateBase
    {
        private StateManager manager;

        private GameObject UIMemoBG;
        private GameObject UIMemoTxt;

        private GameObject TargetObj;
        private MemoObject MemoObj;
        private TextMesh TMObject;
        private GameObject EventSys;

        public VRMemoView(StateManager managerRef, GameObject TargetObject)
        {
            manager = managerRef;
            TargetObj = TargetObject;

            GameObject DisposolObj = GameObject.FindGameObjectWithTag("Disposol");
            if (DisposolObj) GameObject.Destroy(DisposolObj);
            Debug.Log("MemoView");

            EventSys = GameObject.Find("EventSystem");
            UIMemoBG = GameObject.Find("MemoView");
            if (!UIMemoBG)
                Debug.Log("Sel Target is Null");
            UIMemoBG.GetComponent<SpriteRenderer>().enabled = true;

            UIMemoTxt = GameObject.Find("MemoText");
            if (!UIMemoTxt)
                Debug.Log("Sel Text is Null");

            MemoObj = TargetObject.GetComponent<MemoObject>();
            //TargetObject.GetComponent<MemoObject>().MemoPrefab;
            TextMesh T = UIMemoTxt.GetComponent<TextMesh>();
            TMObject = UIMemoTxt.GetComponent<TextMesh>();
            StateManager.InputTextMesh(T, MemoObj.GetMemo());

            //T.text = "New Memo Test";
            //GameObject.Find ("Head").GetComponent<CardboardHead> ().ViewMoveOn = false;
        }

        public void StateUpdate()
        {
            if (Input.GetKeyUp(KeyCode.Q))
                ExitMemoView();
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
                        ExitMemoView();
                        break;

                    case Operation.SELECT:
                        SendSpeechMemoRequest();
                        break;

                    default:
                        break;

                }
            }
        }

        void Switch()
        {
            //Application.LoadLevel("Scene1");
            //manager.SwitchState(new PlayState(manager));


        }

        private void SendSpeechMemoRequest()
        {
            new SpeechRequest().SendRequest(manager, (result) =>
            {
                Debug.Log(result);
                switch (result.Status)
                {
                    case RequestResult.STATUS_SUCCESS:
                        MemoObj.InputMemo(result.Speech);
                        manager.QueueOnMainThread(() => TMObject.text = MemoObj.GetMemo());
                        break;

                    default:
                        Debug.Log("SpeechRequest : failed!");
                        break;
                }
            });
        }

        private void ExitMemoView()
        {
            manager.SwitchState(new VRMemoViewExit(manager, TargetObj));
        }

    }

}
