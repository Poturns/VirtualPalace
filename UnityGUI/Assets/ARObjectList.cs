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
	public int ObjectMAx;
	// Use this for initialization
	void Start() 
	{
		ARDataList = new List<ARObject>();
		
	}

	public override void OnSelect()
	{
		StateManager.GetManager ().SwitchState (new VRRegistARDataState(StateManager.GetManager(), gameObject));
	}
	public void AddARData(ARObject aData)
	{

	}
	public void AddARData(int ResID , string Title , string Resource )
	{
		
	}
	public void CreateARObject()
	{
		string BookCaseName = "BookCaseTrigger9";
		PrefabContainer PrefabStrore = GameObject.Find ("PreLoadPrefab").GetComponent<PrefabContainer> ();

		BookCaseScript BookCase = GameObject.Find (BookCaseName).GetComponent<BookCaseScript>();

		BookCase.CreateBookForAR (PrefabStrore.GetPrefab (OBJ_LIST.BOOK_GROUP_3), KIND_SOURCE.IMAGE 
		                          , ARDataList[Index].Title , ARDataList[Index].Resource);
	}


}
