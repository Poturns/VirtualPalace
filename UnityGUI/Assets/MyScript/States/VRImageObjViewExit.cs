using System;
using UnityEngine;

namespace MyScript.States
{
    [Obsolete()]
    public class VRImageObjViewExit : AbstractGazeInputState
    {
        private GameObject ImageUI;
       

        public VRImageObjViewExit(StateManager managerRef, GameObject TargetObject) : base(managerRef, "VRImageObjectViewEXIT")
        {
            ImageUI = GameObject.Find("ImageView");
            if (!ImageUI)
                Debug.Log("ImageSelector is Null");

            // Apply Change Texture
            Renderer renderer = TargetObject.gameObject.GetComponent<Renderer>();
            if (renderer != null && renderer.materials != null)
            {
                int MatSize = renderer.materials.GetLength(0);
                renderer.materials[MatSize - 1].mainTexture = ImageUI.GetComponent<ImageControl>().GetTexture();
            }

            TargetObject.gameObject.GetComponent<PictureObj>().Path =ImageUI.GetComponent<ImageControl>().GetNowPath();
            ImageUI.GetComponent<MeshCollider>().enabled = false;
            ImageUI.GetComponent<MeshRenderer>().enabled = false;
		
			SetGazeInputMode (GAZE_MODE.OBJECT);
			SetCameraLock (false);
        }

        public override void StateUpdate()
        {
            SwitchState(new VRSceneIdleState(Manager));
        }

        void Switch()
        {
            //Application.LoadLevel("Scene1");
            //manager.SwitchState(new PlayState(manager));
        }

    }
}

