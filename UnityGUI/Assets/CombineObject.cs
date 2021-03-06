﻿using UnityEngine;
using MyScript;
using MyScript.Objects;

public class CombineObject : AbstractBasicObject {

	private MemoObject TextData;
	private PictureObj ImageData;
	private MovieObject MovieData;

	public void Init(KIND_SOURCE kind)
	{
		SetKind (kind);

	}

	public VRObject MakeVRObjectFromBookcase()
	{
		Transform tr = gameObject.transform.parent;

		string ContentStr = GetContentString ();
        VRObject SaveObj = new VRObject.Builder(tr.name, SourceKind, ModelKind)
            .SetID(ID)
            .SetParentName(tr.parent.parent.gameObject.name)
            .SetPosition(tr.position)
            .SetScale(transform.localScale)
            .SetRotation(tr.rotation)
            .SetResTitle("")
            .SetResContents(ContentStr)
            .Build();

		//부모가 피봇 >> 좌표와 이름은 피봇의 좌표와 이름을 사용함
		
		return SaveObj;
	}
	private string GetContentString()
	{
		switch(SourceKind)
		{
		case KIND_SOURCE.TEXT:
			return TextData.Memo;
		case KIND_SOURCE.IMAGE:
			return ImageData.Path;
		case KIND_SOURCE.MOVIE:
			return MovieData.Path;
		default:
			return "";
		}
	}
	private void SetContentString(string ContentsData)
	{
		switch(SourceKind)
		{
		case KIND_SOURCE.TEXT:
			TextData.Memo = ContentsData;
			break;
		case KIND_SOURCE.IMAGE:
			//Updata코드도 추가 되야함
			ImageData.Path = ContentsData;

			break;
		case KIND_SOURCE.MOVIE:
			//Update코드 추가
			MovieData.Path = ContentsData;
			break;
		default:
			Debug.Log ("SetContentsString:: Set Kind First, kind : " + SourceKind);
			break;
		}
	}

	public void UpdateWithSaveObjectData(VRObject vrObject)
	{
		ID = vrObject.ID;
		//포지션값만 기즈모에 적용 x
		gameObject.transform.parent.position =  vrObject.Position;
		// 나머지는 이 오브젝트에 적용
		gameObject.transform.parent.rotation = vrObject.Rotation;
		gameObject.transform.localScale = vrObject.Scale;
		gameObject.transform.parent.name = vrObject.Name;
		SourceKind = vrObject.SourceKind;
		ModelKind = vrObject.ObjKind;
		//Debug.Log ("Loaded Contents String :::::" + vrObject.ResContents);
		SetContentString (vrObject.ResContents);
		//Debug.Log ("CombineObject Update : " + GetContentString());
	}

	public void UpdateContents(string Con , int id)
	{
		SetContentString (Con);
		ID = id;
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
		//Debug.Log ("k :" + k);
		Debug.Log ("CombineObj Kind :" + SourceKind);
	}
	// Update is called once per frame
	void Update () {
	
	}
}
