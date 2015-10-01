using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using MyScript.Interface;


public class BookCaseScript : MonoBehaviour , IObject {

	public GameObject BookPrefab;
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
		Debug.Log ("CreateBook");
		Vector3 NewPos = transform.position;
		NewPos.x += XOffset;
		NewPos.y = BasicY;
		NewPos.z = BasicZ +ZOffset * Cnt; 
		GameObject NewBook = Instantiate (BookPrefab , NewPos , BookPrefab.transform.rotation) as GameObject;
	
		BookList.Add (NewBook);
		Cnt++;
	}
	public void OnSelect()
	{
		CreateBook ();
	}

	// Use this for initialization
	void Start () 
	{
		ZOffset = 0.03f;

		BookList = new List<GameObject> ();
		BasicZ = transform.position.z - GetComponent<BoxCollider>().size.z*0.5f+ZFirstOffset;
		BasicY = transform.position.y - GetComponent<BoxCollider>().size.y*0.5f;



		Ymax = transform.localScale.y;
		ZMax = transform.localScale.z;
		Cnt = 0;
	}

	// Update is called once per frame
	void Update () {
	
	}

}
