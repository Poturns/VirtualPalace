using UnityEngine;
using System.Collections.Generic;
using MyScript.Interface;
using MyScript;


public class ImageControl : AbstractBasicObject {

	private List<AndroidApi.Media.ImageDirInfo> imageDirInfo;
	private int count = 0;
	private Texture2D NowTexture;
	void Start()
	{

	}
	public override void OnSelect()
	{
		NextImg ();
	}
	public Texture2D GetTexture()
	{
		count = 0;
		return NowTexture;
	}
	public string GetNowPath()
	{
		return imageDirInfo [count].FirstInfo.Path;
	}
	public void NextImg ()
	{
		
		if (imageDirInfo == null) {
			imageDirInfo = AndroidApi.Media.ImageDirInfo.GetDirInfoList (AndroidApi.AndroidUtils.GetActivityObject ());
		}
		
		if (count >= imageDirInfo.Count)
			count = 0;
		
		 NowTexture = Utils.Image.Load (imageDirInfo[count++].FirstInfo.Path);
		
		gameObject.GetComponent<Renderer> ().material.mainTexture = NowTexture;
	}
}
