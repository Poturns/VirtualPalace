using UnityEngine;
using System.Collections;
using MyScript.States;
using MyScript.Interface;
using System.Collections.Generic;
using AndroidApi.Media;

public class PictureObj : MonoBehaviour, IRaycastedObject
{


    public string Title
    {
        get;
        set;
    }
    private Texture2D Tex;
    private string Path;

    private List<ImageDirInfo> imageDirList;
    private int count;

    public void OnSelect()
    {
        StateManager.GetManager().SwitchState(new VRImageObjView(StateManager.GetManager(), gameObject));
    }

    // Use this for initialization
    void Start()
    {
        Title = "Test Obj";
    }

    public void PictureUpdate()
    {
        if (imageDirList == null)
            imageDirList = ImageDirInfo.GetDirInfoList(AndroidApi.AndroidUtils.GetActivityObject());

        if (count >= imageDirList.Count)
            count = 0;

        Path = imageDirList[count++].FirstInfo.Path;

        GetComponent<Renderer>().materials[1].mainTexture = Utils.Image.Load(Path);
    }

    // Update is called once per frame
    void Update()
    {

    }
}
