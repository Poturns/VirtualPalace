using UnityEngine;
using UnityEngine.UI;
using System;
using System.Collections;
using System.Collections.Generic;
using AndroidApi.Media;
using UnityApi;

public class VideoScene : BaseScene
{
	List<VideoDirInfo> dirInfoList;
	Image im;
	Text text;
	int count;
	Vector2 vector2 = new Vector2 (0.5f, 0.5f);
	
	void Start ()
	{
		base.Init ();
		
		im = GameObject.FindWithTag ("1").GetComponent<Image> ();
		text = GameObject.FindWithTag ("2").GetComponent<Text> ();
		
		Button btn = GameObject.Find ("Button").GetComponent<Button> ();
		btn.onClick.AddListener (ShowNextImage);
		
		dirInfoList = VideoDirInfo.GetDirInfoList (AndroidApi.AndroidUtils.GetActivityObject ());
		ShowNextImage ();
		
	}
	
	void Update ()
	{
		base.OnUpdate ();
	}

	private void ShowNextImage ()
	{
		text.text = "loading";
		
		if (count >= dirInfoList.Count) {
			count = 0;
		}
		
		try {
			VideoInfo info = dirInfoList [count].FirstInfo;

			Texture2D texture = Util.Image.Load(info.GetFirstFrameThumbnailPath ("abc" + count));
			Rect rect = new Rect (0, 0, texture.width, texture.height);
			Sprite sprite = Sprite.Create (texture, rect, vector2);
			im.sprite = sprite;
			
			text.text = info.DisplayName;
		} finally {
			count++;
		}
	}
}


