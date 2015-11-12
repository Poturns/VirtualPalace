using LitJson;
using System;
using System.Text;

namespace BridgeApi.Controller.Request
{
    public class ARAddRequest : AbsCallbackRequest<bool>
    {

        public static ARAddRequest NewRequest()
        {
            return new ARAddRequest();
        }

        public override void SendRequest(IPlatformBridge bridge, Action<bool> callback)
        {
            bridge.RequestToPlatform(this, jsonResult => {
                JsonData json = JsonMapper.ToObject(jsonResult);

                JsonData result = json["save_new_ar_item"];

                callback(((string)result[RequestResult.RESULT]).Equals(RequestResult.STATUS_SUCCESS));
            });
        }

        public override string ToJson()
        {
            StringBuilder sb = new StringBuilder();
            JsonWriter writer = new JsonWriter(sb);

            writer.WriteObjectStart();
            writer.WritePropertyName("save_new_ar_item");

            writer.WriteObjectStart();
            writer.WritePropertyName("title");
            writer.Write("");
            writer.WriteObjectEnd();

            writer.WriteObjectEnd();

            return sb.ToString();
        }
    }
}
