using UnityEngine;

namespace MyScript.States
{
    public class VRModelSelect : AbstractGazeInputState
	{
		//BookCaseTrigger
		private GameObject Target;

		private UITransform UITrans;
		public VRModelSelect (StateManager managerRef , GameObject TargetObject) : base(managerRef, "VRModelSelectState")
		{
			Target = TargetObject;

            SetGazeInputMode(1);

			GameObject UI = GameObject.Find ("ModelSelectUI");
			UI.GetComponent<UITransform> ().OnOffOUIButton (true);

			UITrans = UI.GetComponent<UITransform> (); 
			UITrans.LockCameraRot ();
		}

        protected override void HandleCancelOperation()
        {
            base.HandleCancelOperation();
        }

		public void EndState()
		{
			UITrans.UnlockCameraRot ();
			GameObject UI = UITrans.gameObject;
			for (int i = 0; i < UI.transform.childCount; i++) 
			{
				UI.transform.GetChild(i).gameObject.SetActive(false);
			}
			
		}
		
	}
}

