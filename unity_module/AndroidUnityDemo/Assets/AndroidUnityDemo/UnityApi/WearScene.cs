using UnityEngine;
using UnityEngine.UI;
using System;
using AndroidApi;

public class WearScene : BaseScene, IWearableMessageListener
{
	Text logText;
	string log = "===Message from Wearable===\n";

	void Start ()
	{
        Init();

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
        OnUpdate();
	}

	#region IWearableMessageListener
	
	public void OnMessageReceived(string path, AndroidJavaObject data){
		ProcessingMessage (data.Call<string> ("toString"));
	}
	
	public void OnMessageReceived(string path, string str){
		ProcessingMessage (str);
	}
		
	#endregion IWearableMessageListener

	
	private void ProcessingMessage(string str)
	{
		QueueOnMainThread (() => {
			log += str + "\n";
			logText.text = log;
		});
	}

}