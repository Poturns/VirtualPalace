using UnityEngine;
using System.Collections;
using UnityEngine.UI;
//using AndroidApi.Media;

public class ExternalInput : MonoBehaviour {

	public void TestCode(){
		string temp = AndroidApi.Media.ImageDirInfo.GetDirInfoList( AndroidApi.AndroidUtils.GetActivityObject ())[0].FirstInfo.Path;

		gameObject.GetComponent<Image>().sprite = Sprite.Create(Utils.Image.Load (temp) ,
		                                                                gameObject.GetComponent<RectTransform>().rect ,
		                                                                gameObject.GetComponent<RectTransform>().pivot);

	}

}
