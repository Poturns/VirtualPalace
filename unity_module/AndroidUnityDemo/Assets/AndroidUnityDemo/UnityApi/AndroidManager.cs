using UnityEngine;
using UnityEngine.UI;
using System;
using System.Collections;
using System.Collections.Generic;
using AndroidApi;
using UnityApi;

public class AndroidManager : MonoBehaviour , ISpeechToTextListener , IWearableMessageListener
{
	AsyncTasker asyncTasker;

	InputHandleHelperProxy inputHandleHelperProxy;
	STTInputHandler sttHandler;
	string log = "";
	WearableInputHandler wearableHandler;

	Text logText, buttonText;
	Button button;


	#region Unity Lifecycle method
	// Use this for initialization
	void Start () {
		asyncTasker = new AsyncTasker ();

		//Debug.Log ("onStart");
		logText = GameObject.FindWithTag ("log").GetComponent<Text>();
		//Debug.Log (logText);

		button = GameObject.Find ("Button").GetComponent<Button>();
		//Debug.Log (button);

		buttonText = button.transform.Find("Text").GetComponent<Text>();
		//Debug.Log (buttonText);
		buttonText.text = "Start";

		button.onClick .AddListener (OnStartButtonClick);

		inputHandleHelperProxy = Utils.GetInputHandleHelperProxy ();
		//Debug.Log (inputHandleHelperProxy);

		sttHandler = inputHandleHelperProxy.GetSTTInputHandler ();
		//Debug.Log (sttHandler);
		sttHandler.SetListener (this);

		wearableHandler = inputHandleHelperProxy.GetWearableInputHandler ();
		wearableHandler.SetListener (this);
	}

	void OnGUI() {
	
	}
	
	// Update is called once per frame
	void Update () {
		asyncTasker.OnUpdate ();
	}

	#endregion Unity Lifecycle method

	#region UI component callback

	public void OnStartButtonClick()
	{
		//Debug.Log ("clicked");
		if (sttHandler.IsInVoiceRecognition ()) {
			buttonText.text = "Start";
			sttHandler.Stop();
		} else {
			buttonText.text = "Stop";
			sttHandler.Start();
		}

	}

	#endregion UI component callback


	public void PrintText(string text)
	{

		//TODO Text Component 에 log 표시
		asyncTasker.QueueOnMainThread (() => {
				log += text;
				logText.text = log;
			}
		);

	}

	#region ISpeechToTextListener

	public void OnReadyForSpeech()
	{
		log = "";
		PrintText ("\n==OnReadyForSpeech==\n");

	}
	
	public void OnBeginningOfSpeech()
	{
		PrintText ("==OnBeginningOfSpeech==\n");
	}
	
	public void OnEndOfSpeech()
	{
		PrintText ("==OnEndOfSpeech==\n\n");

		asyncTasker.QueueOnMainThread (() => buttonText.text = "Start");
	}
	
	public void OnError(int error)
	{
		PrintText ("==OnError : [" + STTInputHandler.GetErrorMessage(error) + "] ==\n\n");


	}
	
	public void OnBufferReceived(byte[] buffer)
	{
		
	}
	
	public void OnResults(bool isPartial, string[] results, float[] confidences)
	{
		string s = "==result==\n";

		for(int i = 0; i < results.Length; i++){
			s += results[i] + " , confidence : " + confidences[i] + "\n";
		}

		PrintText (s + "\n====\n");
	}

	#endregion ISpeechToTextListener


	#region IWearableMessageListener
		
	public void OnMessageReceived(string path, AndroidJavaObject data){
		asyncTasker.QueueOnMainThread (() => {
				log = data.Call<string>("toString");
				logText.text = log;
			}
		);
	}

	#endregion IWearableMessageListener
}
