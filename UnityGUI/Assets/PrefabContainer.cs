using UnityEngine;
using System.Collections;
using System.Collections.Generic;



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
	public GameObject SoccerObj;
	public GameObject DecoObj;
	public GameObject StatuObj;
	public GameObject CandleObj;
	public GameObject HumanObj;
	public GameObject AnimalObj;


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

		case OBJ_LIST.SOCCER_OBJ:
			return SoccerObj;
		case OBJ_LIST.DECO1:
			return DecoObj;
		case OBJ_LIST.STATU_OBJ:
			return StatuObj;
		case OBJ_LIST.CANDLE_OBJ:
			return CandleObj;
		case OBJ_LIST.HUMUN_OBj:
			return HumanObj;
		case OBJ_LIST.ANIMAL_OBJ:
			return AnimalObj;

		default:
			Debug.Log ("Error : GetPrefab : Can't Find Prefab");
			return null;
		}
	}

}
