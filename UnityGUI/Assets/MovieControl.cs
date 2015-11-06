using UnityEngine;
using System.Collections.Generic;
using MyScript;

public class MovieControl : AbstractBasicObject
{

    private List<BridgeApi.Media.VideoDirInfo> VideoDirInfoList;
    private int Index = 0;
    private Texture2D ThumnailTex;
    private string ThumnailPath;
    private Renderer movieControlRenderer;

    public string MoviePath;

    public override void OnSelect()
    {
        PlayMovie();
    }

    public void SetIndex(int index)
    {
        Index = index;
    }

    //해당 인덱스의 동영상패스와 썸네일텍스쳐를 가져오고 썸네일 텍스쳐를 보여줌 
    public void UpdateMovie()
    {
        CheckIndexValidity();

        MoviePath = VideoDirInfoList[Index].FirstInfo.Path;
        ThumnailPath = VideoDirInfoList[Index].FirstInfo.GetFirstFrameThumbnailPath("ThumnailTexture");
        ThumnailTex = Utils.Image.Load(ThumnailPath);

        if (movieControlRenderer == null)
            movieControlRenderer = gameObject.GetComponent<Renderer>();

        movieControlRenderer.material.mainTexture = ThumnailTex;

    }

    public void ShowNextMovieThumbnail()
    {
        ++Index;
        CheckIndexValidity();
        UpdateMovie();
    }

    public void ShowPrevMovieThumbnail()
    {
        --Index;
        CheckIndexValidity();
        UpdateMovie();
    }

    private void CheckIndexValidity()
    {
        if (VideoDirInfoList == null)
        {
            VideoDirInfoList = BridgeApi.Media.MediaFactory.GetInstance().GetVideoDirInfoList();
        }

        if (Index >= VideoDirInfoList.Count)
            Index = 0;
        else if (Index < 0)
            Index = VideoDirInfoList.Count - 1;
    }

    private void PlayMovie()
    {
        if (MoviePath == null)
        {
            Debug.Log("MoviePath is Null");
            return;
        }

        if (!Handheld.PlayFullScreenMovie(MoviePath))
            Debug.Log("PlayMovie Fail");

    }
    public int GetIndex()
    {
        return Index;
    }
}
