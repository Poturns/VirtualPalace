using UnityEngine;
using UnityEngine.UI;
using System.Collections.Generic;
using AndroidApi.Media;

public class GalleryScene : BaseScene
{

	List<ImageDirInfo> imageDirInfoList;
	Image im;
	Text text;
	int count;
	Vector2 vector2 = new Vector2 (0.5f, 0.5f);

	void Start ()
	{
        Init();

		im = GameObject.FindWithTag ("1").GetComponent<Image> ();
		text = GameObject.FindWithTag ("2").GetComponent<Text> ();

		Button btn = GameObject.Find ("Button").GetComponent<Button> ();
		btn.onClick.AddListener (ShowNextImage);

		imageDirInfoList = ImageDirInfo.GetDirInfoList (AndroidApi.AndroidUtils.GetActivityObject ());
		ShowNextImage ();
	
	}

	void Update ()
	{
        OnUpdate();
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
			ImageInfo imageInfo = imageDirInfoList [count].FirstInfo;

			Sprite sprite = CreateSprite (imageInfo.Path);
			im.sprite = sprite;

			text.text = imageInfo.DisplayName;
		} finally {
			count++;
		}
	}

}

