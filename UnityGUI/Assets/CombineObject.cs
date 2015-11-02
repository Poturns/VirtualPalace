using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using MyScript.States;
using MyScript.Interface;
using MyScript;

public class CombineObject : AbstractBasicObject {

	private MemoObject TextData;
	private PictureObj ImageData;
	private MovieObject MovieData;

	public void Init(KIND_SOURCE kind)
	{
		SetKind (kind);

	}
	// Use this for initialization
	void Start () {
	
	}
	void Awake()
	{
		TextData = gameObject.GetComponent<MemoObject> ();
		ImageData = gameObject.GetComponent<PictureObj> ();
		MovieData = gameObject.GetComponent<MovieObject> ();
	}
	public override void OnSelect()
	{
		switch (SourceKind) 
		{
		case KIND_SOURCE.TEXT:
			TextData.OnSelect();
			break;
		case KIND_SOURCE.IMAGE:
			ImageData.OnSelect();
			break;
		case KIND_SOURCE.MOVIE:
			MovieData.OnSelect();
			break;
		}
	}
	public void SetKind(KIND_SOURCE k)
	{
		SourceKind = k;
		Debug.Log ("k :" + k);
		Debug.Log ("CombineObj Kind :" + SourceKind);
	}
	// Update is called once per frame
	void Update () {
	
	}
}
