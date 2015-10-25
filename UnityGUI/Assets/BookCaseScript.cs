using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using MyScript.States;
using MyScript.Interface;


public class BookCaseScript : MonoBehaviour , IRaycastedObject {

	public GameObject BookPrefab;
	public GameObject Pivot;
	public float ZFirstOffset;

	private float ZOffset;
	public float XOffset;
	private float Ymax;
	private float ZMax;

	private float BasicZ;
	private float BasicY;
	private int Cnt;
	private List<GameObject> BookList;

	private void CreateBook()
	{

		GameObject ObjInstance = BookPrefab.transform.GetChild (0).gameObject;
		
		ZOffset = (ObjInstance.GetComponent<BoxCollider>().size.z) * ObjInstance.transform.localScale.z;
		
		BasicZ = Pivot.transform.position.z + ZFirstOffset;
		BasicY = Pivot.transform.position.y;
		
		

		Vector3 NewPos = Pivot.transform.position;
		NewPos.x += XOffset;
		NewPos.y = BasicY;
		NewPos.z = BasicZ +(ZOffset+0.01f) * Cnt; 
		GameObject NewBook = Instantiate (BookPrefab , NewPos , BookPrefab.transform.rotation) as GameObject;
	
		BookList.Add (NewBook);
		Cnt++;
	}
	public void OnSelect()
	{
		//StateManager.GetManager ().SwitchState (new VRObjectSelect (StateManager.GetManager (), gameObject));
		StateManager.GetManager ().SwitchState (new VRModelSelect (StateManager.GetManager (), gameObject));

		//CreateBook ();
	}

	// Use this for initialization
	void Start () 
	{

		Ymax = transform.localScale.y;
		ZMax = transform.localScale.z;
		Cnt = 0;
		BookList = new List<GameObject> ();

	}

	// Update is called once per frame
	void Update () {
	
	}

}
