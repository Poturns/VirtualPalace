using System;
using UnityEngine;

namespace AndroidApi
{
	public class STTInputHandler : IInputHandleHelper<ISpeechToTextListener>
	{
		public STTInputHandler (AndroidJavaObject inputHandleHelper) : base(inputHandleHelper)
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

	}
}

