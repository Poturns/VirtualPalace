using UnityEngine;
using UnityEngine.UI;
using System;
using System.Collections;
using System.Collections.Generic;
using AndroidApi;
using UnityApi;

public class DriveScene : BaseScene
{
	DriveHandler driveHandler;
	Text logText;
	string log;
	
	void Start ()
	{
		base.Init ();

		driveHandler = inputHandleHelperProxy.GetDriveHandler ();
		
		logText = GameObject.Find("Text").GetComponent<Text>();
		Button clearLog = GameObject.Find("Button").GetComponent<Button>();
		clearLog.onClick.AddListener (()=>{
			logText.text = log;
		});
	}
	
	void Update()
	{
		base.OnUpdate ();
	}
	
	private void ProcessingMessage(String str)
	{
		QueueOnMainThread (() => {
			log += str + "\n";
			logText.text = log;
		});
	}
	
}