using UnityEngine;
using System.Collections.Generic;
using UnityEngine.UI;
using AndroidApi.Controller;
using MyScript.Interface;


public class ImageControl : MonoBehaviour , IRaycastedObject {

	private List<AndroidApi.Media.ImageDirInfo> imageDirInfo;
	private int count = 0;
	private Texture2D NowTexture;
	void Start()
	{

	}
	public void OnSelect()
	{
		NextImg ();
	}
	public Texture2D GetTexture()
	{
		count = 0;
		return NowTexture;
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
