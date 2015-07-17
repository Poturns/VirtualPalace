using System;
using UnityEngine;

namespace AndroidApi
{
	public class STTInputHandler : IInputHandleHelper<ISpeechToTextListener>
	{
		internal STTInputHandler (AndroidJavaObject activity, AndroidJavaObject inputHandleHelper) : base(activity, inputHandleHelper)
		{
		}

		public override void SetListener(ISpeechToTextListener listener)
		{
			STTListener sttListener = new STTListener (listener);
			inputHandleHelper.Call ("setListener", sttListener);
		}


		public bool IsInVoiceRecognition()
		{
			return inputHandleHelper.Call<bool> ("isInVoiceRecognition");
		}

		public void StartOrStop()
		{
			if (IsInVoiceRecognition ()) {
				Stop ();
			} else {
				Start();
			}

		}


		public static String GetErrorMessage(int error)
		{
			switch (error) {
			case 1:
				return "ERROR_NETWORK_TIMEOUT";

			case 2:
				return "ERROR_NETWORK";

			case 3:

				return "ERROR_AUDIO";

			case 4:
				return "ERROR_SERVER";

			case 5:
				return "ERROR_CLIENT";

			case 6:
				return "ERROR_SPEECH_TIMEOUT";

			case 7:
				return "ERROR_NO_MATCH";

			case 8:
				return "ERROR_RECOGNIZER_BUSY";

			case 9:
				return "ERROR_INSUFFICIENT_PERMISSIONS";

			default:
				return "ERROR_UNKNOWN";
			}
		}
	}
}

