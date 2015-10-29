using UnityEngine;
using BridgeApi.Controller;
using BridgeApi.Controller.Request;

namespace MyScript.States
{

    public class VRMemoView : AbstractInputHandleState
    {
        private GameObject UIMemoBG;
        private GameObject UIMemoTxt;

        private GameObject TargetObj;
        private MemoObject MemoObj;
        private TextMesh TMObject;
        private GameObject EventSys;

        public VRMemoView(StateManager managerRef, GameObject TargetObject) : base(managerRef, "VRMemoView")
        {
            TargetObj = TargetObject;

            GameObject DisposolObj = GameObject.FindGameObjectWithTag("Disposol");
            if (DisposolObj) GameObject.Destroy(DisposolObj);

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

        public override void StateUpdate()
        {
            if (Input.GetKeyUp(KeyCode.Q))
                ExitMemoView();
        }


        protected override void HandleSelectOperation()
        {
            base.HandleSelectOperation();
            SendSpeechMemoRequest();
        }

        protected override void HandleCancelOperation()
        {
            base.HandleCancelOperation();
            ExitMemoView();
        }
              
        void Switch()
        {
            //Application.LoadLevel("Scene1");
            //manager.SwitchState(new PlayState(manager));


        }

        private void SendSpeechMemoRequest()
        {
            new SpeechRequest().SendRequest(Manager, (result) =>
            {
                Debug.Log(result);
                switch (result.Status)
                {
                    case RequestResult.STATUS_SUCCESS:
                        MemoObj.InputMemo(result.Speech);
                        QueueOnMainThread(() => TMObject.text = MemoObj.GetMemo());
                        break;

                    default:
                        Debug.Log("SpeechRequest : failed!");
                        break;
                }
            });
        }

        private void ExitMemoView()
        {
            SwitchState(new VRMemoViewExit(Manager, TargetObj));
        }

    }

}
