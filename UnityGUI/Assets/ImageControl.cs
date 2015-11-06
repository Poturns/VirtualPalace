using UnityEngine;
using System.Collections.Generic;
using MyScript;


public class ImageControl : AbstractBasicObject
{

    private List<BridgeApi.Media.ImageDirInfo> imageDirInfo;
    private int Index = 0;
    
    private Renderer imageRenderer;
    private string currentPath;

    public string Path
    {
        set { UpdateImageByCurrentPath(value); }
        get { return currentPath; }
    }

    public Texture2D CurrentTexture { get; private set; }

    void Start()
    {

    }

    public override void OnSelect()
    {
        //NextImg ();
    }

    public void ShowNextImageThumbnail()
    {
        ++Index;
        CheckIndexValidity();
        UpdateImage();
    }

    public void ShowPrevImageThumbnail()
    {
        --Index;
        CheckIndexValidity();
        UpdateImage();
    }

    public void UpdateImage()
    {
        CheckIndexValidity();

        UpdateImageByCurrentPath(imageDirInfo[Index].FirstInfo.Path);
    }

    private void UpdateImageByCurrentPath(string path)
    {
        currentPath = path;

        if (path == null)
            return;

        if (imageRenderer == null)
            imageRenderer = gameObject.GetComponent<Renderer>();

        Texture2D newTexture = Utils.Image.Load(path);
        imageRenderer.material.mainTexture = newTexture;

        CurrentTexture = newTexture;
    }

    private void CheckIndexValidity()
    {
        if (imageDirInfo == null)
        {
            imageDirInfo = BridgeApi.Media.MediaFactory.GetInstance().GetImageDirInfoList();
        }

        if (Index >= imageDirInfo.Count)
            Index = 0;
        else if (Index < 0)
            Index = imageDirInfo.Count - 1;
    }

}
