using UnityEngine;
using System.Collections;
using System;

public class ARScreen : MonoBehaviour
{

    // Use this for initialization
    WebCamTexture CamScreen;

    void Start()
    {
        if (CamScreen == null)
            CamScreen = new WebCamTexture();
        GetComponent<Renderer>().material.mainTexture = CamScreen;

        if (CamScreen != null && !CamScreen.isPlaying)
        {
            try
            {
                CamScreen.Play();
            }
            catch (Exception) { }
        }
    }

    // Update is called once per frame
    void Update()
    {

    }

    public void EndCamera()
    {
        if (CamScreen != null && CamScreen.isPlaying)
        {
            try
            {
                CamScreen.Stop();
            }
            catch (Exception) { }
        }
    }

    void OnDisable()
    {
        EndCamera();
    }

}
