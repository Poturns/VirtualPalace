using UnityEngine;
using System.Collections;

public class UITransform : MonoBehaviour {
	public Transform BackUpTrans;
	public KIND_SOURCE CurrentType;
	public GameObject TargetObj;
	public int UIID; // 0 : ModelUI , 1: ModelObjUI
	// Use this for initialization

	// Update is called once per frame
	void Update () 
	{
			
	}
	void RecoverPos()
	{

		//gameObject.transform.position = BackUpTrans.position;
		gameObject.transform.rotation = BackUpTrans.rotation;
	}
	public void LockCameraRot()
	{
		BackUpTrans = gameObject.transform;
		GameObject.Find ("Head").GetComponent<CardboardHead> ().ViewMoveOn = UIID;
	}
	public void UnlockCameraRot()
	{
		GameObject.Find ("Head").GetComponent<CardboardHead> ().ViewMoveOn = 0;
		RecoverPos ();
	}
	public void OnOffOUIButton(bool OnOff)
	{
		for (int i = 0; i < transform.childCount; i++) 
		{
			transform.GetChild(i).gameObject.SetActive(OnOff);
			
		}
	}


}
