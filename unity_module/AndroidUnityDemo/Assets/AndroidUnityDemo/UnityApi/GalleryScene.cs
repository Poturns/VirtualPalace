using UnityEngine;
using UnityEngine.UI;
using System;
using System.Collections;
using System.Collections.Generic;
using AndroidApi.Gallery;
using UnityApi;

public class GalleryScene : BaseScene
{

	List<ImageDirInfo> imageDirInfoList = null;
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

		imageDirInfoList = ImageDirInfo.GetImageDirInfoList (AndroidApi.AndroidUtils.GetActivityObject ());
		ShowNextImage ();
	
	}

	void Update ()
	{
		base.OnUpdate ();
	}

	Sprite CreateSprite (string path)
	{
		Texture2D texture = Util.Image.Load (path);
		Rect rect = new Rect (0, 0, texture.width, texture.height);
		return Sprite.Create (texture, rect, vector2);
	}

	private void ShowNextImage ()
	{
		text.text = "loading";

		if (count >= imageDirInfoList.Count) {
			count = 0;
		}

		try {
			ImageInfo imageInfo = imageDirInfoList [count].FirstImageInfo;

			Sprite sprite = CreateSprite (imageInfo.ImagePath);
			im.sprite = sprite;

			text.text = imageInfo.DisplayName;
		} finally {
			count++;
		}
	}

}

