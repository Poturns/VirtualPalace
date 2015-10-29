using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using MyScript.States;
using MyScript.Interface;

public class CombineObject : MonoBehaviour , IRaycastedObject {

	private MemoObject TextData;
	private PictureObj ImageData;
	public KIND_SOURCE Kind;

	// Use this for initialization
	void Start () {
	
	}
	void Awake()
	{
		TextData = gameObject.GetComponent<MemoObject> ();
		ImageData = gameObject.GetComponent<PictureObj> ();
	}
	public void OnSelect()
	{
		switch (Kind) 
		{
		case KIND_SOURCE.TEXT:
			TextData.OnSelect();
			break;
		case KIND_SOURCE.IMAGE:
			ImageData.OnSelect();
			break;
		}
	}
	public void SetKind(KIND_SOURCE k)
	{
		Kind = k;
		Debug.Log ("k :" + k);
		Debug.Log ("CombineObj Kind :" + Kind);
	}
	// Update is called once per frame
	void Update () {
	
	}
}
