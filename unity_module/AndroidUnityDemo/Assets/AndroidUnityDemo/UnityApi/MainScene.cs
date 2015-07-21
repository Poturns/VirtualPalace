using UnityEngine;
using UnityEngine.UI;
using System;
using AndroidApi;

public class MainScene : MonoBehaviour
{
	void Start()
	{
		Utils.SetOnBackPressListener (() => false);

		foreach (string scene in new String[]{"wear", "stt", "drive"}) {
			string str = scene;
			GameObject.FindWithTag (scene).GetComponent<Button> ().onClick.AddListener (new UnityEngine.Events.UnityAction(() => {
				Application.LoadLevel (str + "_scene"); 
			}));
		}
	}
}