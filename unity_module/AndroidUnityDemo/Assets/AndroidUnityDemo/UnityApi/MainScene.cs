using UnityEngine;
using UnityEngine.UI;
using System;
using AndroidApi;

public class MainScene : MonoBehaviour
{
	void Start()
	{
		AndroidUtils.SetOnBackPressListener (() => false);

		foreach (string scene in new string[]{"wear", "stt", "drive", "gallery", "video"}) {
			string str = scene;
			GameObject.FindWithTag (scene).GetComponent<Button> ().onClick.AddListener (new UnityEngine.Events.UnityAction(() => {
				Application.LoadLevel (str + "_scene"); 
			}));
		}
	}
}