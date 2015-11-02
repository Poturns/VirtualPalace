using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using MyScript.States;
using MyScript.Interface;
using MyScript;


public class BookCaseScript : AbstractBasicObject {

	public GameObject ObjPrefab;
	public GameObject Pivot;
	public float ZFirstOffset;

	private float ZOffset;
	public float XOffset;
	private float Ymax;
	private float ZMax;

	private float ZCurrentPos;

	private float BasicZ;
	private float BasicY;
	private int Cnt;
	private List<GameObject> BookList;

	public KIND_SOURCE CurrentKind;
	private void Init()
	{
		SourceKind = KIND_SOURCE.BOOK_CASE;
		ModelKind = OBJ_LIST.NO_MODEL;
	}
	public void CreateBook()
	{

		GameObject ObjInstance = ObjPrefab.transform.GetChild (0).gameObject;
		
		ZOffset = (ObjInstance.GetComponent<BoxCollider>().size.z) * ObjInstance.transform.localScale.z;
		
		Debug.Log ("BasicZ : " + BasicZ);
	

		Vector3 NewPos = Pivot.transform.position;
		NewPos.x += XOffset;
		NewPos.y = BasicY;
		NewPos.z = BasicZ +ZCurrentPos;

		GameObject NewBook = Instantiate (ObjPrefab , NewPos , ObjPrefab.transform.rotation) as GameObject;
		GameObject RealData = NewBook.transform.GetChild (0).gameObject;
		StateManager.GetManager().ObjCount++;
		NewBook.name = "UserObj" + StateManager.GetManager ().ObjCount;
		RealData.GetComponent<CombineObject> ().Init( CurrentKind);
	
		ZCurrentPos += ZOffset;
		BookList.Add (NewBook);
		Cnt++;
	}
	public override void OnSelect()
	{
		StateManager.GetManager ().SwitchState (new VRModelSelect (StateManager.GetManager (), gameObject));
		GameObject SelectUI = GameObject.Find ("ObjModelSelectUI");
		SelectUI.GetComponent<UITransform>().TargetObj=gameObject;
	
		//StateManager.GetManager ().SwitchState (new VRModelSelect (StateManager.GetManager (), gameObject));

		//CreateBook ();
	}
	public void SetCurrentPrefab(OBJ_LIST Kind)
	{
		PrefabContainer PrefabStore = GameObject.Find ("PreLoadPrefab").GetComponent<PrefabContainer> ();
		ObjPrefab = PrefabStore.GetPrefab (Kind);
		if(ObjPrefab ==null) Debug.Log ("Fail : SetCurrentPrefab");
	}
	public void SetKind(KIND_SOURCE kind)
	{
		CurrentKind = kind;
	}
	// Use this for initialization
	void Start () 
	{
		Init ();
		BasicZ = Pivot.transform.position.z;
		BasicY = Pivot.transform.position.y;

		Ymax = transform.localScale.y;
		ZMax = transform.localScale.z;
		Cnt = 0;
		BookList = new List<GameObject> ();
		ZCurrentPos = 0;//0.01f;

	}

	// Update is called once per frame
	void Update () {
	
	}

}
