using UnityEngine;
using System.Collections;

public class ScreenToWorldTest : MonoBehaviour {

	public float zOffset;
	// Use this for initialization
	void Start () 
	{
		zOffset = GameObject.Find ("ARView").transform.position.z / 2;
	}
	
	// Update is called once per frame
	void Update () 
	{
		transform.position =Camera.main.ScreenToWorldPoint (new Vector3 (100, 100, Camera.main.nearClipPlane+zOffset));
	}
}
