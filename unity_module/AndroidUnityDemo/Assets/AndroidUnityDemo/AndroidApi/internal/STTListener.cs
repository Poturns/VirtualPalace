using UnityEngine;

namespace AndroidApi 
{
	internal class STTListener : AndroidJavaProxy
	{
		private ISpeechToTextListener listener;

		internal STTListener(ISpeechToTextListener listener) : base(Utils.SpeechToTextListenerClassName)
		{
			this.listener = listener;
		}

		public void onReadyForSpeech()
		{
			listener.OnReadyForSpeech ();
		}
		
		public void onBeginningOfSpeech()
		{
			listener.OnBeginningOfSpeech ();
		}
		
		public void onEndOfSpeech()
		{
			listener.OnEndOfSpeech ();
		}
		
		public void onError(int error)
		{
			listener.OnError(error);
		}
		
		public void onBufferReceived(byte[] buffer)
		{
			listener.OnBufferReceived (buffer);
		}
		
		public void onResults(bool isPartial, string[] resultList, float[] confidences)
		{
			listener.OnResults (isPartial, resultList, confidences);
		}

	}
}
