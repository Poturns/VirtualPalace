using UnityEngine;
using UnityEngine.UI;
using System;
using System.Collections;
using System.Collections.Generic;
using AndroidApi.Drive;
using UnityApi;
using System.IO;

public class DriveScene : BaseScene
{
	DriveHandler driveHandler;
	Text logText;
	string log;
	
	void Start ()
	{
		base.Init ();

		driveHandler = inputHandleHelperProxy.GetDriveHandler ();

		Button create = GameObject.FindWithTag ("1").GetComponent<Button> ();
		create.onClick.AddListener (ProcessCreateDummyFileInAppFolder);

		Button dummy = GameObject.FindWithTag ("2").GetComponent<Button> ();
		dummy.onClick.AddListener (ProcessQueryFileDummyFileInAppFolder);

		logText = GameObject.FindWithTag ("3").GetComponent<Text> ();

	}
	
	void Update ()
	{
		base.OnUpdate ();
	}
	
	private void ProcessingMessage (String str)
	{
		//QueueOnMainThread (() => {
		log += str;
		logText.text = log;
		//});
	}

	private DriveFile CreateDummyFileInAppFolder ()
	{
		return  driveHandler.GetAppFolder ().CreateFile (driveHandler, "appconfig.txt", "text/plain", driveHandler.NewDriveContents ());
	}
	
	private MetadataBuffer QueryFileDummyFileInAppFolder ()
	{
		return driveHandler.GetAppFolder ().QueryChildrenByTitle (driveHandler, "appconfig.txt");
	}

	private void ProcessCreateDummyFileInAppFolder ()
	{
		log = "";
		logText.text = log;

		DriveFile file = CreateDummyFileInAppFolder ();
		ProcessingMessage ("\n+Create Dummy File in App Folder\n");

		if (file == null) 
			ProcessingMessage ("+Error while trying to create the file\n");
		else
			ProcessingMessage ("+Created a file in App Folder : " + file.GetDriveId ().GetStringForm () + "\n");
	}

	private void ProcessQueryFileDummyFileInAppFolder ()
	{
		ProcessingMessage ("\n+query Dummy File in App Folder\n");

		MetadataBuffer metadataBuffer = QueryFileDummyFileInAppFolder ();
		
		if (metadataBuffer == null) {
			ProcessingMessage ("Error while trying to read a dummy file\n");
			return;
		}
		
		
		DriveContents contents = driveHandler.OpenFile (metadataBuffer.Get (0).GetDriveId (), DriveFile.MODE_READ_WRITE);
		
		ProcessingMessage ("+Operation : query Dummy File in App Folder success\n"
			+ "+trying to open Dummy File\n");
		
		if (contents == null) {
			metadataBuffer.Release ();
			ProcessingMessage ("Error while trying to read a dummy file\n");
			return;
		}

		String content = contents.OpenContentsToString (driveHandler, "utf-8");
		if (content == null) {
			ProcessingMessage ("+reading a dummy file has failed\n");
			metadataBuffer.Release ();
			return;
		}
		
		ProcessingMessage (
			"+Dummy File Contents : \n----------- Contents ---------\n" + content
			+ "\n------------------------------------\n" +
			"+appending \'hello world\' to Dummy File\n");

		if (contents.WriteContents (driveHandler, "hello world\n")) {
			ProcessingMessage ("+writing success\n");
		} else {
			ProcessingMessage ("+writing has failed\n");
		}

		if (contents.Commit (driveHandler).IsSuccess ()) {
			ProcessingMessage ("+appending success\n");
		} else {
			ProcessingMessage ("+appending success\n");
		}

		metadataBuffer.Release ();
	
	}

}