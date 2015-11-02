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

			VoiceIcon = GameObject.Find("VoiceIcon");
			if(VoiceIcon == null) Debug.Log ("Voice Icon is NULL");

			SetGazeInputMode (GAZE_MODE.OFF);
			SetCameraLock (true);

            //T.text = "New Memo Test";
            //GameObject.Find ("Head").GetComponent<CardboardHead> ().ViewMoveOn = false;
        }

        public override void StateUpdate()
        {
            if (Input.GetKeyUp(KeyCode.Q))
                ExitMemoView();
        }

		// 현재상태에서의 선택은 충돌체크가 아니라 바로 음성인식 실행 
        protected override void HandleSelectOperation()
        {
			//
            //base.HandleSelectOperation();
            
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
			MeshRenderer IconRenderer = VoiceIcon.GetComponent<MeshRenderer> ();
			if(IconRenderer == null) Debug.Log("VoiceIcon is Null");
			IconRenderer.enabled = true;
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
			IconRenderer.enabled = false;
        }

        private void ExitMemoView()
        {
            SwitchState(new VRMemoViewExit(Manager, TargetObj));
        }

    }

}
