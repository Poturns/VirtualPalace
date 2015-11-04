using System.Collections.Generic;
using BridgeApi.Controller;
using UnityEngine;

namespace MyScript.States
{
    public class VRImageObjView : AbstractGazeInputState
    {
        private GameObject ImageUI;
        private GameObject Target;

        private ImageControl imageControl;

        private MeshCollider imageMeshCollider;
        private MeshRenderer imageMeshRenderer;

        public VRImageObjView(StateManager managerRef, GameObject TargetObject) : base(managerRef, "VRImageObjectView")
        {
            Target = TargetObject;

            DestroyMarkedObject();

            FindImageView();

            imageControl = ImageUI.GetComponent<ImageControl>();

            imageMeshCollider = ImageUI.GetComponent<MeshCollider>();
            imageMeshRenderer = ImageUI.GetComponent<MeshRenderer>();

            LockCameraAndMesh(true);

			//Load? ????? Image UI ?? ?? ?? 
			PictureObj PicObj = Target.GetComponent<PictureObj> ();
			if (PicObj != null) 
			{
				if(PicObj.Path != null && PicObj.Path == "")
				{
					//????? ???? ???
					imageControl.Path = PicObj.Path;
				}
			}
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
            // FindImageView();

            //ImageUI.GetComponent<AbstractBasicObject>().OnSelect();
            ExitImageState(true);
        }

        protected override void HandleDirectionOperation(Dictionary<int, Direction> directionDictionary)
        {
            //base.HandleDirectionOperation(directionDictionary);
            foreach (int key in directionDictionary.Keys)
            {
                //ChooseMovie(directionDictionary[key])
                // 명령이 여러번 오는 경우 썸네일 로딩 낭비를 막기 위하여 한번만 썸네일 변경을 실시한다.
                if (NavigateImages(directionDictionary[key]))
                    return;
            }
        }

        private bool NavigateImages(Direction direction)
        {
            switch (direction.Value)
            {
                case Direction.EAST:
                    imageControl.ShowNextImageThumbnail();
                    break;
                case Direction.WEST:
                    imageControl.ShowPrevImageThumbnail();
                    break;
                default:
                    return false;
            }
            return true;
        }


        protected override void HandleCancelOperation()
        {
            //base.HandleCancelOperation();
            ExitImageState(false);
        }

        private void ApplyImageTexture()
        {

        }

        void ExitImageState(bool applyImageTexture)
        {
            Debug.Log("=============== Exit VRImageObjectView");

            // Legacy code
            // SwitchState(new VRImageObjViewExit(Manager, Target));


            FindImageView();
            
            // Apply Change Texture
            if (applyImageTexture)
            {
                Renderer renderer = Target.gameObject.GetComponent<Renderer>();
                ImageControl imageControl = ImageUI.GetComponent<ImageControl>();

                if (renderer != null && renderer.materials != null)
                {
                    int MatSize = renderer.materials.GetLength(0);
                    renderer.materials[MatSize - 1].mainTexture = imageControl.GetTexture();
                }

                Target.gameObject.GetComponent<PictureObj>().Path = imageControl.GetNowPath();

                Debug.Log("=============== Apply Image Texture");
            }
            else
            {
                Debug.Log("=============== Discard Image Texture");
            }

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

