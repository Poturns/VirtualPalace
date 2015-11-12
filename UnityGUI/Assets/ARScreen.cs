using UnityEngine;
using System.Collections;
using System;

public class ARScreen : MonoBehaviour
{

    // Use this for initialization
    WebCamTexture CamScreen;
    void Start()
    {
        CamScreen = new WebCamTexture();
        GetComponent<Renderer>().material.mainTexture = CamScreen;

        try
        {
            CamScreen.Play();
		
        }
        catch (Exception) { }
    }
	public void EndCamera()
	{
		CamScreen.Stop();
	}
    // Update is called once per frame
    void Update()
    {

    }
}
