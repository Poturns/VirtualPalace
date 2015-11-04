using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using System.Runtime.Serialization.Formatters.Binary;
using System.IO;

using MyScript;

public  class SaveLoader : MonoBehaviour {

	public const string ObjFileName= "SceneData.dat"; 
	public const string BookCaseFileName= "CaseData.dat"; 

	// Use this for initialization
	public void SavetoFile()
	{
		//Find Root
		GameObject Root = GameObject.Find ("BookCaseGroup");
		Stream Objstream = File.Open(Application.dataPath + ObjFileName , FileMode.Create);
		Stream BookCasestream = File.Open(Application.dataPath + BookCaseFileName , FileMode.Create);

		BinaryFormatter bformatter = new BinaryFormatter();
		//bformatter.Binder = new Ver

		//각 객체는 Gizmo 자식으로 Ins
		for(int i = 0; i < Root.transform.childCount ; i++)
		{
			//GetChild(0) -> Gizmo;
			AbstractBasicObject BookCase = Root.transform.GetChild(i).GetChild(0).gameObject.GetComponent<AbstractBasicObject>();
			SaveDataForBookCase bData = (SaveDataForBookCase)BookCase.GetSaveData();
			bformatter.Serialize (BookCasestream, bData);
			Debug.Log (bData.ObjName +" ChildCnt: " +BookCase.transform.childCount);
			for(int j = 0 ; j < BookCase.transform.childCount ; j++)
			{
				//여기도 각 객체 gizmo;
				AbstractBasicObject InteractObj =  BookCase.transform.GetChild(j).GetChild(0).gameObject.GetComponent<AbstractBasicObject>();
				if(InteractObj == null) Debug.Log(BookCase.transform.parent.name +"Child"+j+":null");
				SaveData sData = InteractObj.GetSaveData();
				bformatter.Serialize (Objstream, sData);
			}
		}
		Objstream.Close ();
		BookCasestream.Close ();

	}

	public void LoadToFile()
	{
		Debug.Log ("Call LoadToFile");
		BinaryFormatter bf = new BinaryFormatter();
		ReadBookCaseFile (bf);

	}
	// 리턴값은 총 오브젝트가 저장된 갯수 
	private int ReadBookCaseFile(BinaryFormatter bf)
	{
		Stream BCStream = File.Open (Application.dataPath + BookCaseFileName , FileMode.Open); 
		int ObjCnt = 0;
		int Count = GameObject.Find ("BookCaseGroup").transform.childCount;
		for (int i = 0; i < Count; i++) 
		{
			SaveDataForBookCase bData = new SaveDataForBookCase();

			bData = (SaveDataForBookCase)bf.Deserialize(BCStream);
			Debug.Log("BookCase" + i + " Name" +" : " +bData.ObjName);
		}
		return ObjCnt;
	}
	private void ReadObjFile()
	{}
}
