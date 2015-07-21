using UnityEngine;
using UnityEngine.UI;
using System;
using System.Collections;
using System.Collections.Generic;
using AndroidApi;
using UnityApi;

public class WearScene : BaseScene, IWearableMessageListener
{
	Text logText;
	string log = "===Message from Wearable===\n";

	void Start ()
	{
		base.Init ();

		WearableInputHandler wearableHandler = inputHandleHelperProxy.GetWearableInputHandler ();
		wearableHandler.SetListener (this);

		logText = GameObject.FindWithTag("Player").GetComponent<Text>();

		Button clearLog = GameObject.Find("Button").GetComponent<Button>();
		clearLog.onClick.AddListener (()=>{
			log = "===Message from Wearable===\n";
			logText.text = log;
		});
	}

	void Update()
	{
		base.OnUpdate ();
	}

	#region IWearableMessageListener
	
	public void OnMessageReceived(string path, AndroidJavaObject data){
		ProcessingMessage (data.Call<string> ("toString"));
	}
	
	public void OnMessageReceived(string path, string str){
		ProcessingMessage (str);
	}
		
	#endregion IWearableMessageListener

	
	private void ProcessingMessage(String str)
	{
		QueueOnMainThread (() => {
			log += str + "\n";
			logText.text = log;
		});
	}

}