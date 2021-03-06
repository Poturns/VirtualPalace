﻿using UnityEngine;
using System.Collections;
using System.IO;
using System.Text;
using MyScript.Interface;
using MyScript.Objects;

public enum KIND_SOURCE
{
	TEXT,
	IMAGE,
	MOVIE,
	BOOK_CASE,
	UI_BUTTON
};

public enum OBJ_LIST
{
	AR_OBJ = 0,
	BOOK_GROUP_1 = 1,
	BOOK_GROUP_2,
	BOOK_GROUP_3,
	DISH_OBJ,
	NOACT_OBJ,
	WOOD_OBJ,
	CAP_OBJ,
	PICTURE_OBJ,
	SOCCER_OBJ,
	DECO1,
	STATU_OBJ,
	CANDLE_OBJ,
	HUMUN_OBj,
	ANIMAL_OBJ,
	UI,
	NO_MODEL
	
};

namespace MyScript
{
	//OnSelect기반의 모든 오브젝트의 기본이 되는 베이스 클래스
	[System.Serializable]
	public abstract class AbstractBasicObject :   MonoBehaviour ,IRaycastedObject 
	{
		public KIND_SOURCE SourceKind;
		public OBJ_LIST ModelKind;
		protected string ObjName;
		//객체의 간단한 설명 (섬네일 에서 보여줄 예정)
		protected string Discription;
		public int ID;

		public AbstractBasicObject(){}

	
	/*
		//파일로 저장
		public virtual Object GetSaveObjectData()
		{
			/*
			Transform tr = gameObject.transform.parent;
			VRObject SaveObj = new VRObject (ID, tr.name, tr.parent.gameObject.name,(int)ModelKind
			                                 ,tr.position, tr.rotation, transform.localScale, (int)SourceKind
			                                 ,"","");
			//부모가 피봇 >> 좌표와 이름은 피봇의 좌표와 이름을 사용함

			return null;
		}
		public virtual
		public virtual void UpdateContents(string Con, int id)
		{
		}
		//SaveData 받아서 그 데이터를 바탕으로 속성값 갱신
		public virtual void UpdateWithSaveObjectData(Object sData)
		{
			//gameObject.transform.position =  sData.Pos;
			//gameObject.transform.rotation = sData.Rot;
			//gameObject.transform.localScale = sData.Scale;
			//gameObject.transform.parent.name = sData.ObjName;
			//SourceKind = sData.Source;
			//ModelKind = sData.ObjKind;
		}
		*/
		//DB로 저장
		public virtual bool SaveToDB()
		{
			return true;
		}

		public virtual void OnSelect()
		{

		}
	
	}
}