﻿using System;
using System.Text;

namespace BridgeApi.Controller.Request
{
    public class SpeechRequest : IRequest
    {
        public static SpeechRequest NewRequest()
        {
            return new SpeechRequest();
        }

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

        public void SendRequest(IPlatformBridge bridge, Action<SpeechRequestResult> callback)
        {
            bridge.RequestToPlatform(this, (result) =>
            {
                callback(JsonInterpreter.ParseSpeechResultFromPlatform(result));
            });
        }
    }
}
