using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using MyScript.States;
using MyScript.Interface;
using MyScript;

public class ARObjectList : AbstractBasicObject 
{
	private List<ARObject> ARDataList;
	public int Index = 0;
	public int ObjectMax = 0;
	// Use this for initialization
	void Start() 
	{
		ARDataList = new List<ARObject>();

	}

	public override void OnSelect()
	{
		StateManager.GetManager ().SwitchState (new VRRegistARDataState(StateManager.GetManager(), gameObject));
	}
	public void AddARData(SaveData aData)
	{
		ARObject NewARData = new ARObject (aData);
		ARDataList.Add (NewARData);
		ObjectMax++;
	}
	public void AddARData(int ResID , string Title , string Resource )
	{
		
	}
	public bool CreateARObject()
	{
		if (Index == ObjectMax)
			return false;
		string BookCaseName = "BookCaseTrigger9";
		PrefabContainer PrefabStrore = GameObject.Find ("PreLoadPrefab").GetComponent<PrefabContainer> ();

		BookCaseScript BookCase = GameObject.Find (BookCaseName).GetComponent<BookCaseScript>();
		if (BookCase == null)
			Debug.Log ("ARBufer ::::BookCase Is Null");
		else
			BookCase.CreateBookForAR (PrefabStrore.GetPrefab (OBJ_LIST.BOOK_GROUP_3), KIND_SOURCE.IMAGE 
		                          , ARDataList[Index].Title , ARDataList[Index].Resource);
		return true;
	}


}
