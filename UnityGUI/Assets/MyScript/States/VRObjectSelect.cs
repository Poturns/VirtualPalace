using UnityEngine;

namespace MyScript.States
{
    public class VRObjectSelect : AbstractGazeInputState
	{
		//BookCaseTrigger
		private GameObject Target;
		private UITransform UITrans;
		public VRObjectSelect (StateManager managerRef , GameObject TargetObject) : base(managerRef,"VRObjectSelectState")
		{
			Target = TargetObject;
			GameObject UI = GameObject.Find ("ModelSelectUI");
			for (int i = 0; i < UI.transform.childCount; i++) 
			{
				UI.transform.GetChild(i).gameObject.SetActive(true);

			}
			UITrans = UI.GetComponent<UITransform> (); 
			UITrans.LockCameraRot ();
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

