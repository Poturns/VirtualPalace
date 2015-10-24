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
	PICTURE_OBJ
};
public class PrefabContainer : MonoBehaviour 
{


	private GameObject DishObj;
	private GameObject NoActionObj;
	private GameObject BookGroup1;
	private GameObject BookGroup2;
	private GameObject BookGroup3;
	private GameObject WoodDoll1;
	private GameObject CapObj;
	private GameObject PictureFrameObj;

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
	public GameObject GetBookPrefab(int ObjName)
	{
		switch (ObjName) 
		{
		case (int)OBJ_LIST.BOOK_GROUP_1:
			return BookGroup1;
		case (int)OBJ_LIST.BOOK_GROUP_2:
			return BookGroup2;
		case (int)OBJ_LIST.BOOK_GROUP_3:
			return BookGroup3;
		}
		return null;
	}
	public GameObject GetPrefab(int ObjName)
	{
		switch (ObjName) 
		{
		case (int)OBJ_LIST.CAP_OBJ:
			return CapObj;
		case (int)OBJ_LIST.DISH_OBJ:
			return DishObj;
		case (int)OBJ_LIST.NOACT_OBJ:
			return NoActionObj;
		case (int)OBJ_LIST.PICTURE_OBJ:
			return PictureFrameObj;
		case (int)OBJ_LIST.WOOD_OBJ:
			return WoodDoll1;
		default:
			Debug.Log ("Error : GetPrefab : Can't Find Prefab");
			return null;
		}
	}

}
