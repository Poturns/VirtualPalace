using System;
using System.Text;

namespace AndroidApi.Controller.Request
{
    public class SpeechRequest : IRequest
    {
        public string ToJson()
        {
            StringBuilder sb = new StringBuilder();
            LitJson.JsonWriter writer = new LitJson.JsonWriter(sb);

            writer.WriteObjectStart();
            writer.WritePropertyName(SpeechRequestResult.SPEECH_REQUEST_KEY);
            writer.Write(SpeechRequestResult.SPEECH_REQUEST_KEY);
            writer.WriteObjectEnd();

            return sb.ToString();
        }

        public void SendRequest(Action<SpeechRequestResult> callback)
        {
            AndroidUnityBridge.GetInstance().RequestToAndroid(this, (result) =>
            {
                callback(JsonInterpreter.ParseSpeechResultFromAndroid(result));
            });
        }
    }
}
