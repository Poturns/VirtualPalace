using UnityEngine;
using System.Collections.Generic;
using MyScript.Interface;
using BridgeApi.Controller;

namespace MyScript.States
{
    public class VRImageObjViewExit : IStateBase
    {
        private StateManager manager;

        private GameObject ImageUI;


        public VRImageObjViewExit(StateManager managerRef, GameObject TargetObject)
        {
            manager = managerRef;


            Debug.Log("VRImageObjectViewEXIT");

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

            TargetObject.gameObject.GetComponent<PictureObj>().SetPath(ImageUI.GetComponent<ImageControl>().GetNowPath());
            ImageUI.GetComponent<MeshCollider>().enabled = false;
            ImageUI.GetComponent<MeshRenderer>().enabled = false;
        }
        public void StateUpdate()
        {
            manager.SwitchState(new VRSceneIdleState(manager));
        }
        public void ShowIt()
        {

        }
        public void InputHandling(List<Operation> InputOp)
        {

        }
        void Switch()
        {
            //Application.LoadLevel("Scene1");
            //manager.SwitchState(new PlayState(manager));


        }
    }
}

