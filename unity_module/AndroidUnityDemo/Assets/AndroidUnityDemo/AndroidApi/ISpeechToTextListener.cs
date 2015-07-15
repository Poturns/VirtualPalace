namespace AndroidApi {
	public interface ISpeechToTextListener {
		void OnReadyForSpeech();
		
		void OnBeginningOfSpeech();
		
		void OnEndOfSpeech();
		
		void OnError(int error);
		
		void OnBufferReceived(byte[] buffer);
		
		void OnResults(bool isPartial, string[] results, float[] confidences);
	}
}