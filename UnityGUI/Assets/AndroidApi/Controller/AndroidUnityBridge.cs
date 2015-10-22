using BridgeApi.Controller;
using System;
using UnityEngine;
using BridgeApi.Controller.Request;

namespace AndroidApi.Controller
{

    public class AndroidUnityBridge : IPlatformBridgeDelegate
    {
        /// <summary>
        /// java 형태의 AndroidUnityBridge 객체
        /// </summary>
        private AndroidJavaObject javaAndroidUnityBridge;

        public AndroidUnityBridge()
        {
            if (Application.platform == RuntimePlatform.Android)
            {
                javaAndroidUnityBridge = AndroidUtils.GetActivityObject().Call<AndroidJavaObject>("getAndroidUnityBridge");
            }
            else
            {
                javaAndroidUnityBridge = null;
            }
        }

        public bool RequestToPlatform(IRequest request, Action<string> callback)
        {
            if (javaAndroidUnityBridge != null)
                return javaAndroidUnityBridge.Call<bool>("requestCallbackToAndroid", request.ToJson(), new InternalIAndroidUnityCallback(callback));
            else
                return false;
        }

        public void RespondToPlatform(long id, string jsonResult)
        {
            if (javaAndroidUnityBridge != null)
                javaAndroidUnityBridge.Call("respondCallbackToAndroid", id, jsonResult);
        }

        public bool SendSingleMessageToPlatform(string jsonMessage)
        {
            if (javaAndroidUnityBridge != null)
                return javaAndroidUnityBridge.Call<bool>("sendSingleMessageToAndroid", jsonMessage);
            else
                return false;
        }

    }
}
