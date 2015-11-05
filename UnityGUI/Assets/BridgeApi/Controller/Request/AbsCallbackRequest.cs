using System;

namespace BridgeApi.Controller.Request
{
    public abstract class AbsCallbackRequest<CallbackResult> : IRequest
    {
        public abstract string ToJson();

        public abstract void SendRequest(IPlatformBridge bridge, Action<CallbackResult> callback);
    }
}
