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

		int TotalObjCnt = 0;
		//각 객체는 Gizmo 자식으로 Ins
		for(int i = 0; i < Root.transform.childCount ; i++)
		{
			//GetChild(0) -> Gizmo;
			AbstractBasicObject BookCase = Root.transform.GetChild(i).GetChild(0).gameObject.GetComponent<AbstractBasicObject>();
			SaveDataForBookCase bData = (SaveDataForBookCase)BookCase.GetSaveData();
			bformatter.Serialize (BookCasestream, bData);
		//	Debug.Log (bData.ObjName +" ChildCnt: " +BookCase.transform.childCount);
			TotalObjCnt+=BookCase.transform.childCount;
			for(int j = 0 ; j < BookCase.transform.childCount ; j++)
			{
				//여기도 각 객체 gizmo;
				AbstractBasicObject InteractObj =  BookCase.transform.GetChild(j).GetChild(0).gameObject.GetComponent<AbstractBasicObject>();

				if(InteractObj == null) Debug.Log(BookCase.transform.parent.name +"Child"+j+":null");
				SaveData sData = InteractObj.GetSaveData();
				//Debug.Log ("Saved ObjName : "+sData.ObjName);
				//Debug.Log (sData.ObjName + " ParentsName :" + sData.ParentName);
				bformatter.Serialize (Objstream, sData);
			}
		}
		Objstream.Close ();
		BookCasestream.Close ();
		//Debug.Log ("Save Total Obj : " + TotalObjCnt);
	}

	public void LoadToFile()
	{
		Debug.Log ("Call LoadToFile");
		BinaryFormatter bf = new BinaryFormatter();

		int ObjectToLoadCnt = ReadBookCaseFile (bf);

		ReadObjFile (bf , ObjectToLoadCnt);

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
		//	Debug.Log("BookCase" + i + " Name" +" : " +bData.ObjName + "Cnt : " + bData.Cnt);
			//Book Case Instance -> Gizmo의 Child(0)
			BookCaseScript BookCase = GameObject.Find (bData.ObjName).transform.GetChild(0).GetComponent<BookCaseScript>();
			ObjCnt+=bData.Cnt;
			BookCase.UpdateWithSaveData(bData);

		}
		BCStream.Close ();
		//Debug.Log ("Total Object In BookCase : " + ObjCnt);
		return ObjCnt;
	}
	private void ReadObjFile(BinaryFormatter bf , int ObjCnt)
	{
		Stream ObjStream = File.Open (Application.dataPath + ObjFileName , FileMode.Open); 
		PrefabContainer PrefabCon = GameObject.Find("PreLoadPrefab").GetComponent<PrefabContainer>();
		for (int i = 0; i < ObjCnt; i++) 
		{
			SaveData sData = new SaveData();
			sData = (SaveData)bf.Deserialize(ObjStream);
			GameObject Obj = GameObject.Find (sData.ObjName);

			//처음부터 존재 하는 오브젝트일때 컨텐츠 내용만 업데이트
			if(Obj != null)
			{
				AbstractBasicObject RealObj = Obj.transform.GetChild(0).gameObject.GetComponent<AbstractBasicObject>();
				RealObj.UpdateContents(sData.Contents);
			}
			// 추가되어야할 오브젝트일때 생성후 초기화
			else
			{
				GameObject PrefabToLoad = PrefabCon.GetPrefab(sData.ObjKind);
				GameObject LoadObject = Instantiate(PrefabToLoad , sData.Pos , sData.Rot) as GameObject;
				LoadObject.transform.parent = GameObject.Find(sData.ParentName).transform.GetChild(0);
				LoadObject.transform.GetChild(0).GetComponent<AbstractBasicObject>().UpdateWithSaveData(sData);

			}
		}
	}
}
