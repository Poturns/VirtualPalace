using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using System.Runtime.Serialization.Formatters.Binary;
using System.IO;

using MyScript;
using BridgeApi.Controller.Request.Database;
using MyScript.objects;
using System;

public class SaveLoader : MonoBehaviour
{

    public const string ObjFileName = "SceneData.dat";
    public const string BookCaseFileName = "CaseData.dat";
    private readonly object COUNT_LOCK = new object();

    // Use this for initialization
    public void SavetoFile(Action callback)
    {
        InsertToDatabase(callback);
    }


    public void InsertToDatabase(Action callback)
    {
        GameObject Root = GameObject.Find("BookCaseGroup");
        int TotalObjCnt = 0;
        StateManager manager = StateManager.GetManager();

        for (int i = 0; i < Root.transform.childCount; i++)
        {
			BookCaseScript BookCase = Root.transform.GetChild(i).GetChild(0).gameObject.GetComponent<BookCaseScript>();
			BookCaseObject bData = BookCase.GetSaveObjectData();

           
            DatabaseRequests.UpdateBookCaseObjects(manager, bData, result =>
            {
                Debug.Log("query ==== " + result);

                manager.QueueOnMainThread(() =>
                {
                    TotalObjCnt += BookCase.transform.childCount;

                    List<VRObject> vrObjectList = new List<VRObject>();
                    for (int j = 0; j < BookCase.transform.childCount; j++)
                    {
                        CombineObject InteractObj = BookCase.transform.GetChild(j).GetChild(0).gameObject.GetComponent<CombineObject>();

                        VRObject sData = InteractObj.GetSaveObjectData();
                        if (sData != null)
                            vrObjectList.Add(sData);
                        
                    }

                    InsertVRObjectsToDatabase(vrObjectList);

                    callback();
                });
            });
        }
    }

    private void InsertVRObjectsToDatabase(List<VRObject> list)
    {
        DatabaseRequests.InsertVRObjects(StateManager.GetManager(), list, result => Debug.Log("query ==== " + result));
    }

    public void LoadFromDatabase()
    {
       // int ObjCnt = 0;
        StateManager manager = StateManager.GetManager();
        DatabaseRequests.QueryBookCaseObjects(manager, results =>
        {
            manager.QueueOnMainThread(() =>
            {
                foreach (BookCaseObject data in results)
                {
                    BookCaseScript BookCase = GameObject.Find(data.Name).transform.GetChild(0).GetComponent<BookCaseScript>();
                   // ObjCnt += data.Cnt;
                    BookCase.UpdateWithSaveObjectData(data);
                }

                RequestSaveDataFromDatabase();
            });
        });
       
    }

    public void LoadToFile()
    {
        Debug.Log("Call LoadToFile");
        //BinaryFormatter bf = new BinaryFormatter();

        //int ObjectToLoadCnt = ReadBookCaseFile (bf);
        //if (ObjectToLoadCnt <0)
        //	return;

        //ReadObjFile (bf, ObjectToLoadCnt);

        LoadFromDatabase();
    }

 
    private void RequestSaveDataFromDatabase()
    {
        DatabaseRequests.QueryVRObjects(StateManager.GetManager(),
                  result =>
                  {
                      StateManager.GetManager().QueueOnMainThread(() =>
                      {
                          foreach (VRObject s in result)
                              UpdataBookDataWithSaveData(s);
                      });
                  });
    }

    private void UpdataBookDataWithSaveData(VRObject sData)
    {
        PrefabContainer PrefabCon = GameObject.Find("PreLoadPrefab").GetComponent<PrefabContainer>();
        GameObject Obj = GameObject.Find(sData.Name);
        //처음부터 존재 하는 오브젝트일때 컨텐츠 내용만 업데이트
        if (Obj != null)
        {
			CombineObject RealObj = Obj.transform.GetChild(0).gameObject.GetComponent<CombineObject>();
            RealObj.UpdateContents(sData.ResContents , sData.ID);
        }
        // 추가되어야할 오브젝트일때 생성후 초기화
        else
        {
            GameObject PrefabToLoad = PrefabCon.GetPrefab(sData.ObjKind);
            GameObject LoadObject = Instantiate(PrefabToLoad, sData.Position, sData.Rotation) as GameObject;
            LoadObject.transform.parent = GameObject.Find(sData.ParentName).transform.GetChild(0);
			LoadObject.transform.GetChild(0).GetComponent<CombineObject>().UpdateWithSaveObjectData(sData);
        }
    }
	/*
    // 리턴값은 총 오브젝트가 저장된 갯수 
    private int ReadBookCaseFile(BinaryFormatter bf)
    {
        int ObjCnt = 0;
        try
        {
            Stream BCStream = File.Open(Application.dataPath + BookCaseFileName, FileMode.Open);


            int Count = GameObject.Find("BookCaseGroup").transform.childCount;
            for (int i = 0; i < Count; i++)
            {
                SaveDataForBookCase bData = new SaveDataForBookCase();

                bData = (SaveDataForBookCase)bf.Deserialize(BCStream);
                //	Debug.Log("BookCase" + i + " Name" +" : " +bData.ObjName + "Cnt : " + bData.Cnt);
                //Book Case Instance -> Gizmo의 Child(0)
                BookCaseScript BookCase = GameObject.Find(bData.ObjName).transform.GetChild(0).GetComponent<BookCaseScript>();
                ObjCnt += bData.Cnt;
                BookCase.UpdateWithSaveData(bData);

            }
            BCStream.Close();
        }
        catch (FileNotFoundException e)
        {
            ObjCnt = -999;
        }

        //Debug.Log ("Total Object In BookCase : " + ObjCnt);
        return ObjCnt;
    }
    private bool ReadObjFile(BinaryFormatter bf, int ObjCnt)
    {
        try
        {
            Stream ObjStream = File.Open(Application.dataPath + ObjFileName, FileMode.Open);
            PrefabContainer PrefabCon = GameObject.Find("PreLoadPrefab").GetComponent<PrefabContainer>();
            StateManager.GetManager().ObjCount = ObjCnt - 16;
            for (int i = 0; i < ObjCnt; i++)
            {
                SaveData sData = new SaveData();
                sData = (SaveData)bf.Deserialize(ObjStream);
                GameObject Obj = GameObject.Find(sData.ObjName);

                //처음부터 존재 하는 오브젝트일때 컨텐츠 내용만 업데이트
                if (Obj != null)
                {
                    AbstractBasicObject RealObj = Obj.transform.GetChild(0).gameObject.GetComponent<AbstractBasicObject>();
                    RealObj.UpdateContents(sData.Contents , sData.Key);
                }
                // 추가되어야할 오브젝트일때 생성후 초기화
                else
                {
                    GameObject PrefabToLoad = PrefabCon.GetPrefab(sData.ObjKind);
                    GameObject LoadObject = Instantiate(PrefabToLoad, sData.Pos, sData.Rot) as GameObject;
                    LoadObject.transform.parent = GameObject.Find(sData.ParentName).transform.GetChild(0);
                    LoadObject.transform.GetChild(0).GetComponent<AbstractBasicObject>().UpdateWithSaveData(sData);

                }
            }
            ObjStream.Close();

            return true;
        }
        catch (FileNotFoundException e)
        {
            return false;
        }

    }
	*/
	private void BufferingARObjectData(SaveData sData)
	{
		
		ARObjectList ARObjBuffer = GameObject.Find ("ABBufferObject").GetComponent<ARObjectList>();
		
		ARObjBuffer.AddARData (sData);
	}
}
