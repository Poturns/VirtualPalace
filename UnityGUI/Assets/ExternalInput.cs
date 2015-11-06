using UnityEngine;
using System.Collections.Generic;
using UnityEngine.UI;

public class ExternalInput : MonoBehaviour
{

	private List<BridgeApi.Media.ImageDirInfo> imageDirInfo;
	private int count = 0;

    void Start()
    {
       
    }

	public void TestCode ()
	{
	
		if (imageDirInfo == null) {
            imageDirInfo = BridgeApi.Media.MediaFactory.GetInstance().GetImageDirInfoList();
		}

		if (count >= imageDirInfo.Count)
			count = 0;

		Texture2D texture = Utils.Image.Load (imageDirInfo[count++].FirstInfo.Path);

		gameObject.GetComponent<Image> ().sprite = Sprite.Create (texture,
		                                                        new Rect (0, 0, texture.width, texture.height),
		                                                                gameObject.GetComponent<RectTransform> ().pivot);
	}

}
