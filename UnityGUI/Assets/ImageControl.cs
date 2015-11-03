using UnityEngine;
using System.Collections.Generic;
using MyScript;


public class ImageControl : AbstractBasicObject {

	private List<AndroidApi.Media.ImageDirInfo> imageDirInfo;
	private int Index = 0;
	private Texture2D NowTexture;
    private Renderer imageRenderer;

    void Start()
	{

	}

	public override void OnSelect()
	{
		//NextImg ();
	}

	public Texture2D GetTexture()
	{
		Index = 0;
		return NowTexture;
	}

	public string GetNowPath()
	{
		return imageDirInfo [Index].FirstInfo.Path;
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

        string currentThumbPath = imageDirInfo[Index].FirstInfo.Path;

        Texture2D newTexture = Utils.Image.Load(imageDirInfo[Index].FirstInfo.Path);

        if (imageRenderer == null)
            imageRenderer = gameObject.GetComponent<Renderer>();

        imageRenderer.material.mainTexture = newTexture;
        NowTexture = newTexture;
    }

    private void CheckIndexValidity()
    {
        if (imageDirInfo == null)
        {
            imageDirInfo = AndroidApi.Media.ImageDirInfo.GetDirInfoList(AndroidApi.AndroidUtils.GetActivityObject());
        }

        if (Index >= imageDirInfo.Count)
            Index = 0;
        else if (Index < 0)
            Index = imageDirInfo.Count - 1;
    }

}
