using UnityEngine;
using System.Collections;

public class ARScreen : MonoBehaviour {

	// Use this for initialization
	WebCamTexture CamScreen;
	void Start () {
		CamScreen = new WebCamTexture ();
		GetComponent<Renderer>().material.mainTexture = CamScreen;
		CamScreen.Play ();

	}
	
	// Update is called once per frame
	void Update () {
	
	}
}
