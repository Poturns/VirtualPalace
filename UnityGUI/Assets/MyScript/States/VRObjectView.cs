using UnityEngine;

namespace MyScript.States
{
    public class VRObjectView : AbstractGazeInputState
	{
		private GameObject UIBookMesh;
		private GameObject UITitleTextObj;
        private TextMesh TMTitle;
		private GameObject Target;
		private GameObject EventSys;

		public VRObjectView (StateManager managerRef , GameObject TargetObject) : base(managerRef, "VRObjectView")
		{
			Target = TargetObject;

			GameObject DisposolObj = GameObject.FindGameObjectWithTag ("Disposol");
			if(DisposolObj)GameObject.Destroy (DisposolObj);

			EventSys = GameObject.Find ("EventSystem");
			UIBookMesh = GameObject.Find ("UIBook");

			//터치시 UI Object 초기화
		

			if (!UIBookMesh)
				Debug.Log ("Sel Target is Null");
			//UIBookMesh.GetComponent<MeshRenderer> ().enabled = true;
			GameObject ShowObj = TargetObject.GetComponent<MemoObject> ().UIObject; 
			GameObject DisposalObj;

			DisposalObj =GameObject.Instantiate (ShowObj ,UIBookMesh.transform.position
				                                     , TargetObject.transform.rotation) as GameObject;

			DisposalObj.transform.GetChild(0).tag = "Disposol";
			DisposalObj.transform.SetParent (UIBookMesh.transform);
			UITitleTextObj = GameObject.Find ("UITitleText");
			if (!UITitleTextObj)
				Debug.Log ("Sel Text is Null");

			//TargetObject.GetComponent<MemoObject>().MemoPrefab;
			string NewTxt = TargetObject.GetComponent<MemoObject>().Title;
			TextMesh T = UITitleTextObj.GetComponent<TextMesh> ();
            TMTitle = T;
            //T.text = NewTxt;
            StateManager.InputTextMesh (T, NewTxt);
			//GameObject.Find ("Head").GetComponent<CardboardHead> ().ViewMoveOn = false;
			SetGazeInputMode (GAZE_MODE.OFF);
			SetCameraLock (true);
		}

		public override void StateUpdate()
		{
            base.StateUpdate();
			// Input
				
			//ChangeMemoScene ();

		
		}

        protected override void HandleCancelOperation()
        {
            base.HandleCancelOperation();

            //UIBookMesh.GetComponent<MeshRenderer> ().enabled = false;
            TMTitle.text = "";
            SwitchState(new VRSceneIdleState(Manager));
        }

        protected override void HandleSelectOperation()
        {
			//base 호출 필요 없음 무조건 메모씬으로 진입 
            //base.HandleSelectOperation();

            ChangeMemoScene();
        }
             
		void ChangeMemoScene()
		{
			UIBookMesh.GetComponent<MeshRenderer> ().enabled = false;
            TMTitle.text = "";
			SwitchState (new VRMemoView (Manager, Target));

		}

	}

}

