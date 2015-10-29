using UnityEngine;
using System.Collections;
using System.Collections.Generic;

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
	HUMUN_OBj

};

public class PrefabContainer : MonoBehaviour 
{


	public GameObject DishObj;
	public GameObject NoActionObj;
	public GameObject BookGroup1;
	public GameObject BookGroup2;
	public GameObject BookGroup3;
	public GameObject WoodDoll1;
	public GameObject CapObj;
	public GameObject PictureFrameObj;

	public static PrefabContainer instanceRef;

	void Awake()
	{
		if (instanceRef == null)
		{

			instanceRef = this;
			DontDestroyOnLoad(gameObject);
		
		}
		else
		{
			DestroyImmediate(gameObject);
		}
	}

	public GameObject GetPrefab(OBJ_LIST ObjName)
	{
		switch (ObjName) 
		{
		case OBJ_LIST.BOOK_GROUP_1:
			return BookGroup1;
		case OBJ_LIST.BOOK_GROUP_2:
			return BookGroup2;
		case OBJ_LIST.BOOK_GROUP_3:
			return BookGroup3;
		case OBJ_LIST.CAP_OBJ:
			return CapObj;
		case OBJ_LIST.DISH_OBJ:
			return DishObj;
		case OBJ_LIST.NOACT_OBJ:
			return NoActionObj;
		case OBJ_LIST.PICTURE_OBJ:
			return PictureFrameObj;
		case OBJ_LIST.WOOD_OBJ:
			return WoodDoll1;
		default:
			Debug.Log ("Error : GetPrefab : Can't Find Prefab");
			return null;
		}
	}

}
