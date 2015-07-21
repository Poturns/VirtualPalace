using UnityEngine;
using UnityEngine.UI;
using System;
using System.Collections;
using System.Collections.Generic;
using AndroidApi;
using UnityApi;

public class STTScene : BaseScene , ISpeechToTextListener
{

	STTInputHandler sttHandler;

	string log = "";

	Text logText, buttonText;
	Button button;

	
	void Start () {
		base.Init ();

		sttHandler = inputHandleHelperProxy.GetSTTInputHandler ();
		sttHandler.SetListener (this);

		initUI ();

	}
	
	void Update()
	{
		base.OnUpdate ();
	}

	private void initUI()
	{
		logText = GameObject.FindWithTag ("log").GetComponent<Text>();

		button = GameObject.Find ("Button").GetComponent<Button>();

		buttonText = button.transform.Find("Text").GetComponent<Text>();
		buttonText.text = "Start";
		
		button.onClick .AddListener (() => {
			if (sttHandler.IsInVoiceRecognition ()) {
				buttonText.text = "Start";
				sttHandler.Stop ();
			} else {
				buttonText.text = "Stop";
				sttHandler.Start ();
			}
		});

	}


	public void PrintText(string text)
	{
		QueueOnMainThread (() => {
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

		QueueOnMainThread (() => buttonText.text = "Start");
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

}
