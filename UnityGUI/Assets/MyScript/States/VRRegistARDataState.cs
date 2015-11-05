using System.Collections.Generic;
using BridgeApi.Controller;
using UnityEngine;

namespace MyScript.States
{
	public class VRRegistARDataState : AbstractGazeInputState
	{

		private GameObject ImageUI;

		//ARObject버퍼
		private ARObjectList Target;
		
		private ImageControl imageControl;
		private MeshCollider imageMeshCollider;
		private MeshRenderer imageMeshRenderer;
		//Target == ARObjectList

		public VRRegistARDataState(StateManager managerRef, GameObject TargetObject) : base(managerRef, "VRRegistARDataState")
		{

			Target = TargetObject.GetComponent<ARObjectList>();
			
			DestroyMarkedObject();
			
			FindImageView();
			
			imageControl = ImageUI.GetComponent<ImageControl>();
			
			imageMeshCollider = ImageUI.GetComponent<MeshCollider>();
			imageMeshRenderer = ImageUI.GetComponent<MeshRenderer>();
			
			LockCameraAndMesh(true);

		}
		
		private void FindImageView()
		{
			if (ImageUI == null)
			{
				ImageUI = GameObject.Find("ImageView");
				if (ImageUI == null)
					Debug.LogWarning("ImageSelector is Null");
			}
		}
		
		protected override void HandleSelectOperation()
		{
			Target.CreateARObject ();
			// FindImageView();
			//ImageUI.GetComponent<AbstractBasicObject>().OnSelect();
		}

		protected override void HandleCancelOperation()
		{
			//base.HandleCancelOperation();
			ExitState();
		}

		void ExitState()
		{
			Debug.Log("=============== Exit VRRegistARDataState");

			FindImageView();
			LockCameraAndMesh(false);
			
			SwitchState(new VRSceneIdleState(Manager));
			
		}
		
		private void LockCameraAndMesh(bool isLock)
		{
			ChangeMeshState(isLock);
			SetGazeInputMode(isLock? GAZE_MODE.OFF: GAZE_MODE.OBJECT);
			SetCameraLock(isLock);
		}
		
		private void ChangeMeshState(bool enable)
		{
			imageMeshRenderer.enabled = enable;
			imageMeshCollider.enabled = enable;
		}
		
	}
}

