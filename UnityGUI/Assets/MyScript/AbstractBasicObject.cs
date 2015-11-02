using UnityEngine;
using System.Collections;

using MyScript.Interface;

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
	UI,
	NO_MODEL
	
};

namespace MyScript
{
	//OnSelect기반의 모든 오브젝트의 기본이 되는 베이스 클래스
	public abstract class AbstractBasicObject :   MonoBehaviour ,IRaycastedObject 
	{
		public KIND_SOURCE SourceKind;
		public OBJ_LIST ModelKind;
		protected string ObjName;
		//객체의 간단한 설명 (섬네일 에서 보여줄 예정)
		protected string Discription; 

	

		//파일로 저장
		public virtual bool SaveToByteStream()
		{
			return true;
		}

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