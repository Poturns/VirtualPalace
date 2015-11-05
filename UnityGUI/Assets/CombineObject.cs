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
	public override SaveData GetSaveData()
	{
		SaveData ForSave = new SaveData ();
		//부모가 피봇 >> 좌표와 이름은 피봇의 좌표와 이름을 사용함
		Transform tr = gameObject.transform.parent;
		string ContentStr = GetContentString (); 

		//Parents >(부모)> 오브젝트 기즈모의 책장인스턴스 >(부모)>책장의 기즈모 이름
		// 포지션은 기즈모의 포지션 , 회전도 부모?
		ForSave.InitData(tr.name,tr.position , tr.rotation
		                 ,transform.localScale ,(int)SourceKind , (int)ModelKind, tr.parent.parent.gameObject.name , ContentStr ,"");

		Debug.Log ("Contents String :::::" + ForSave.Contents);
		return ForSave;
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
			Debug.Log ("SetContentsString:: Set Kind First");
			break;
		}
	}
	public override void UpdateWithSaveData(SaveData sData)
	{
		base.UpdateWithSaveData (sData);
		//포지션값만 기즈모에 적용 
		gameObject.transform.parent.position =  sData.Pos;
		// 나머지는 이 오브젝트에 적용
		gameObject.transform.parent.rotation = sData.Rot;
		gameObject.transform.localScale = sData.Scale;
		Debug.Log ("Loaded Contents String :::::" + sData.Contents);
		SetContentString (sData.Contents);
		Debug.Log ("CombineObject Update : " + GetContentString());
	}
	public override void UpdateContents(string Con)
	{
		SetContentString (Con);
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
