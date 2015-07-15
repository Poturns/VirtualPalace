using UnityEngine;
using UnityEngine.UI;
using System.Collections;
using AndroidApi;

public class AndroidManager : MonoBehaviour , ISpeechToTextListener , IWearableMessageListener
{
	InputHandleHelperProxy inputHandleHelperProxy;
	STTInputHandler sttHandler;
	string log = "";
	//public WearableInputHandler wearableHandler;

	Text logText, buttonText;
	Button button;

	#region Unity Lifecycle method
	// Use this for initialization
	void Start () {
		//Debug.Log ("onStart");

		logText = GameObject.Find ("Text").GetComponent<Text>();
		//Debug.Log (logText);

		button = GameObject.Find ("Button").GetComponent<Button>();
		//Debug.Log (button);

		buttonText = button.GetComponentInChildren<Text> ();
		//Debug.Log (buttonText);
		buttonText.text = "Start";

		button.onClick .AddListener (OnStartButtonClick);

		inputHandleHelperProxy = Utils.GetInputHandleHelperProxy ();
		//Debug.Log (inputHandleHelperProxy);

		sttHandler = inputHandleHelperProxy.GetSTTInputHandler ();
		//Debug.Log (sttHandler);
		sttHandler.SetListener (this);

		//wearableHandler = inputHandleHelperProxy.GetWearableInputHandler ();
		//wearableHandler.SetListener (this);
	}

	void OnGUI() {
	
	}
	
	// Update is called once per frame
	void Update () {
	
	}

	#endregion Unity Lifecycle method

	#region UI component callback

	public void OnStartButtonClick()
	{
		Debug.Log ("clicked");
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
		log += text;

		//TODO Text Component 에 log 표시
		logText.text = log;
	}

	#region ISpeechToTextListener

	public void OnReadyForSpeech()
	{
		PrintText ("\n==OnReadyForSpeech==\n");
	}
	
	public void OnBeginningOfSpeech()
	{
		PrintText ("==OnBeginningOfSpeech==\n");
	}
	
	public void OnEndOfSpeech()
	{
		PrintText ("==OnEndOfSpeech==\n\n");
	}
	
	public void OnError(int error)
	{
		PrintText ("==OnError : + [" + error + "] ==\n\n");
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

	}

	#endregion IWearableMessageListener
}
