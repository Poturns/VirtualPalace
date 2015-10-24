using UnityEngine;
using System.Collections;

public class UITransform : MonoBehaviour {
	public Transform BackUpTrans;
	// Use this for initialization

	// Update is called once per frame
	void Update () 
	{
			
	}
	void RecoverPos()
	{
		gameObject.transform.position = BackUpTrans.position;
		gameObject.transform.rotation = BackUpTrans.rotation;
	}
	public void LockCameraRot()
	{
		BackUpTrans = gameObject.transform;
		GameObject.Find ("Head").GetComponent<CardboardHead> ().ViewMoveOn = false;
	}
	public void UnlockCameraRot()
	{
		GameObject.Find ("Head").GetComponent<CardboardHead> ().ViewMoveOn = true;
		RecoverPos ();
	}
}
