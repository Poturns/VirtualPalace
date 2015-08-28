using UnityEngine;
using LitJson;

namespace AndroidApi
{
    internal class STTListener : AndroidJavaProxy
	{
		private ISpeechToTextListener listener;

		internal STTListener(ISpeechToTextListener listener) : base(AndroidUtils.SpeechToTextListenerClassName)
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
		
		public void onResults(bool isPartial, AndroidJavaObject resultList, float[] confidences)
		{	
			Debug.Log (resultList.Call<string>("toString") + " / " + confidences.ToString());
		}

		public void onResults(bool isPartial, string resultJSON)
		{
			Debug.Log (resultJSON);

			JsonData jData = JsonMapper.ToObject(resultJSON);
			int count = jData.Count;

			string[] results = new string[count];
			float[] confidences = new float[count];

			for (int i = 0 ; i < count ; i++)
			{
				string result = (string)jData[i]["result"];
				float confidence = float.Parse((string)jData[i]["confidence"]);
				results[i] = result;
				confidences[i] = confidence;
			}

			listener.OnResults (isPartial, results, confidences);
		}

	}
}
