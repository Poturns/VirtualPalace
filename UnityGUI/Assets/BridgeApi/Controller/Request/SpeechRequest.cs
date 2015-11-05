using System;
using System.Text;

namespace BridgeApi.Controller.Request
{
    public class SpeechRequest : AbsCallbackRequest<SpeechRequestResult>
    {
        public static SpeechRequest NewRequest()
        {
            return new SpeechRequest();
        }

        public override string ToJson()
        {
            StringBuilder sb = new StringBuilder();
            LitJson.JsonWriter writer = new LitJson.JsonWriter(sb);

            writer.WriteObjectStart();
            writer.WritePropertyName(SpeechRequestResult.SPEECH_REQUEST_KEY);

            writer.WriteArrayStart();

            writer.WriteObjectStart();
            writer.WritePropertyName(SpeechRequestResult.SPEECH_MODE_KEY);
            writer.Write(SpeechRequestResult.SPEECH_MODE_TEXT);
            writer.WriteObjectEnd();

            writer.WriteObjectStart();
            writer.WritePropertyName("action");
            writer.Write("start");
            writer.WriteObjectEnd();

            writer.WriteArrayEnd();

            writer.WriteObjectEnd();

            return sb.ToString();
        }

        public override void SendRequest(IPlatformBridge bridge, Action<SpeechRequestResult> callback)
        {
            bridge.RequestToPlatform(this, (result) =>
            {
                callback(JsonInterpreter.ParseSpeechResultFromPlatform(result));
            });
        }
    }
}
