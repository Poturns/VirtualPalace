using UnityEngine;
using System.Collections;
using AndroidApi;

public class AndroidManager : MonoBehaviour , ISpeechToTextListener , IWearableMessageListener
{
	public STTInputHandler sttHandler;
	private string log = "";
	//public WearableInputHandler wearableHandler;
	//public GUIText mLogText;
	//public GUIText mButton;

	#region Unity Lifecycle method
	// Use this for initialization
	void Start () {
		InputHandleHelperProxy inputHandleHelperProxy = Utils.GetInputHandleHelperProxy ();
		sttHandler = inputHandleHelperProxy.GetSTTInputHandler ();
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
		sttHandler.StartOrStop ();
	}

	#endregion UI component callback


	public void PrintText(string text)
	{
		log += text;

		//TODO Text Component 에 log 표시
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
