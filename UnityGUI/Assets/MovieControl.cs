using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using MyScript.Interface;
using MyScript;

public class MovieControl : AbstractBasicObject  {

	private List<AndroidApi.Media.VideoDirInfo> VideoDirInfoList;
	private int Index = 0;
	private Texture2D ThumnailTex;
	private string ThumnailPath;
	public string MoviePath;

	public override void OnSelect()
	{
		PlayMovie ();
	}
	public void SetIndex(int index)
	{
		Index = index;
	}
	//해당 인덱스의 동영상패스와 썸네일텍스쳐를 가져오고 썸네일 텍스쳐를 보여줌 
	public void UpdateMovie()
	{
		if (VideoDirInfoList == null) {
			VideoDirInfoList = AndroidApi.Media.VideoDirInfo.GetDirInfoList (AndroidApi.AndroidUtils.GetActivityObject ());
		}
		
		if (Index >= VideoDirInfoList.Count)
			Index = 0;
		else if (Index < 0)
			Index = VideoDirInfoList.Count-1;

		MoviePath = VideoDirInfoList [Index].FirstInfo.Path;
		ThumnailPath = VideoDirInfoList [Index].FirstInfo.GetFirstFrameThumbnailPath ("ThumnailTexture");
		ThumnailTex = Utils.Image.Load (ThumnailPath);
		
		gameObject.GetComponent<Renderer> ().material.mainTexture = ThumnailTex;

	}
	//영상리스트 앞뒤 이동
	public void ChangeMovie(int Sign)
	{
		if (Sign >= 0)
			Index++;
		else
			Index--;
		UpdateMovie ();
	}
	private void PlayMovie()
	{
		if (MoviePath == null) {
			Debug.Log ("MoviePath is Null");
			return;
		}

		if(!Handheld.PlayFullScreenMovie (MoviePath))
			Debug.Log("PlayMovie Fail");

	}
	public int GetIndex()
	{
		return Index;
	}
}
