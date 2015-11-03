using UnityEngine;
using BridgeApi.Controller;
using BridgeApi.Controller.Request;

namespace MyScript.States
{

    public class VRMemoView : AbstractGazeInputState
    {
        private GameObject UIMemoBG;
        private GameObject UIMemoTxt;

        private GameObject TargetObj;
        private MemoObject MemoObj;
        private TextMesh TMObject;
        private GameObject VoiceIcon;
        private MeshRenderer IconRenderer;

        private SpriteRenderer MemoBackgroundSpriteRenderer;

        public VRMemoView(StateManager managerRef, GameObject TargetObject) : base(managerRef, "VRMemoView")
        {
            TargetObj = TargetObject;

            DestroyMarkedObject();

            UIMemoBG = GameObject.Find("MemoView");
            if (!UIMemoBG) Debug.Log("Sel Target is Null");

            MemoBackgroundSpriteRenderer = UIMemoBG.GetComponent<SpriteRenderer>();

            UIMemoTxt = GameObject.Find("MemoText");
            if (!UIMemoTxt) Debug.Log("Sel Text is Null");

            MemoObj = TargetObject.GetComponent<MemoObject>();

            //TargetObject.GetComponent<MemoObject>().MemoPrefab;
            //TextMesh T = UIMemoTxt.GetComponent<TextMesh>();

            TMObject = UIMemoTxt.GetComponent<TextMesh>();
            Utils.Text.InputTextMesh(TMObject, MemoObj.Memo);

            VoiceIcon = GameObject.Find("VoiceIcon");
            if (VoiceIcon == null) Debug.Log("Voice Icon is NULL");

            IconRenderer = VoiceIcon.GetComponent<MeshRenderer>();

            LockCameraAndMesh(true);
        }


        public override void StateUpdate()
        {
            if (Input.GetKeyUp(KeyCode.Q))
                ExitMemoView();
        }

        // 현재상태에서의 선택은 충돌체크가 아니라 바로 음성인식 실행 
        protected override void HandleSelectOperation()
        {
            //base.HandleSelectOperation();

            SendSpeechMemoRequest();
        }

        protected override void HandleCancelOperation()
        {
            // base.HandleCancelOperation();
            ExitMemoView();
        }


        private void SendSpeechMemoRequest()
        {
            //MeshRenderer IconRenderer = VoiceIcon.GetComponent<MeshRenderer> ();
            //if(IconRenderer == null) Debug.Log("VoiceIcon is Null");

            IconRenderer.enabled = true;

            SpeechRequest.NewRequest().SendRequest(Manager, result =>
            {
                Debug.Log(result);
                System.Action action;
                switch (result.Status)
                {
                    case RequestResult.STATUS_SUCCESS:
                        MemoObj.Memo = result.Speech;
                        action = () =>
                         {
                             TMObject.text = MemoObj.Memo;
                             IconRenderer.enabled = false;
                         };
                        break;

                    default:
                        Debug.Log("SpeechRequest : failed!");
                        action = () => IconRenderer.enabled = false;
                        break;
                }

                QueueOnMainThread(action);

            });


        }

        private void ExitMemoView()
        {
            // Legacy code
            //SwitchState(new VRMemoViewExit(Manager, TargetObj));

            DestroyMarkedObject();

            TMObject.text = "";

            LockCameraAndMesh(false);

            SwitchState(new VRObjectView(Manager, TargetObj));
        }

        private void LockCameraAndMesh(bool isLock)
        {
            ChangeMeshState(isLock);
            SetGazeInputMode(isLock ? GAZE_MODE.OFF : GAZE_MODE.OBJECT);
            SetCameraLock(isLock);
        }

        private void ChangeMeshState(bool enable)
        {
            MemoBackgroundSpriteRenderer.enabled = enable;
        }


    }

}
