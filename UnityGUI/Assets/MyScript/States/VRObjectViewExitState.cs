using UnityEngine;

namespace MyScript.States
{
    [System.Obsolete()]
    public class VRObjectViewExitState : AbstractGazeInputState
    {
        private GameObject UIBookMesh;
        private GameObject UITitleTextObj;

        public VRObjectViewExitState(StateManager managerRef) : base(managerRef, "VRObjectViewExit")
        {
            DestroyMarkedObject();

            UIBookMesh = GameObject.Find("UIBook");
            if (!UIBookMesh) Debug.Log("Sel Target is Null");

            //UIBookMesh.GetComponent<MeshRenderer> ().enabled = false;


            UITitleTextObj = GameObject.Find("UITitleText");
            if (!UITitleTextObj) Debug.Log("Sel Text is Null");

            UITitleTextObj.GetComponent<TextMesh>().text = "";
            SetGazeInputMode(GAZE_MODE.OBJECT);
            SetCameraLock(false);
        }

        public override void StateUpdate()
        {
            SwitchState(VRSceneIdleState.CopyFromCurrentState(this));
        }


    }


}

