using UnityEngine;

namespace MyScript.States
{
    public class VRObjectView : AbstractGazeInputState
    {
        private GameObject UIBookMesh;
        private GameObject UITitleTextObj;
        private TextMesh TMTitle;
        private GameObject Target;
        private MeshRenderer meshRenderer;

        public VRObjectView(StateManager managerRef, GameObject TargetObject) : base(managerRef, "VRObjectView")
        {
            Target = TargetObject;

            DestroyMarkedObject();

            UIBookMesh = GameObject.Find("UIBook");
            //터치시 UI Object 초기화
            if (UIBookMesh == null) Debug.LogWarning("Sel Target is Null");

            meshRenderer = UIBookMesh.GetComponent<MeshRenderer>();

            MemoObject memoObject = TargetObject.GetComponent<MemoObject>();
            GameObject ShowObj = memoObject.UIObject;
            GameObject DisposalObj = GameObject.Instantiate(ShowObj, UIBookMesh.transform.position
                                                     , TargetObject.transform.rotation) as GameObject;

            DisposalObj.transform.GetChild(0).tag = DESTROY_MARK;
            DisposalObj.transform.SetParent(UIBookMesh.transform);

            UITitleTextObj = GameObject.Find("UITitleText");
            if (UITitleTextObj == null) Debug.LogWarning("Sel Text is Null");

            //TargetObject.GetComponent<MemoObject>().MemoPrefab;

            string NewTxt = memoObject.Title;
            TextMesh T = UITitleTextObj.GetComponent<TextMesh>();
            //T.text = NewTxt;
            Utils.Text.InputTextMesh(T, NewTxt);

            TMTitle = T;

            //GameObject.Find ("Head").GetComponent<CardboardHead> ().ViewMoveOn = false;
            LockCameraAndMesh(true);
        }

        public override void StateUpdate()
        {
            //base.StateUpdate();
            // Input
        }

        protected override void HandleCancelOperation()
        {
            //base.HandleCancelOperation();
            //SwitchState(new VRSceneIdleState(Manager));

            DestroyMarkedObject();

            TMTitle.text = "";
            LockCameraAndMesh(false);

            SwitchState(VRSceneIdleState.CopyFromCurrentState(this));
        }

        protected override void HandleSelectOperation()
        {
            //base 호출 필요 없음 무조건 메모씬으로 진입 
            //base.HandleSelectOperation();

            EnterMemoState();
        }

        void EnterMemoState()
        {
            ChangeMeshState(false);
            TMTitle.text = "";
            SwitchState(new VRMemoView(Manager, Target));
        }

        private void LockCameraAndMesh(bool isLock)
        {
           // ChangeMeshState(isLock);
            SetGazeInputMode(isLock ? GAZE_MODE.OFF : GAZE_MODE.OBJECT);
            SetCameraLock(isLock);
        }

        private void ChangeMeshState(bool enable)
        {
            meshRenderer.enabled = enable;
        }

    }

}

