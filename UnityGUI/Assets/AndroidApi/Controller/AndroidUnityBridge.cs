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
            InitJavaAndroidUnityBridgeReference();
        }

        private void InitJavaAndroidUnityBridgeReference()
        {
            if (Application.platform == RuntimePlatform.Android)
            {
                if (javaAndroidUnityBridge == null)
                {
                    //AndroidJNIHelper.debug = true;
                    javaAndroidUnityBridge = AndroidUtils.GetActivityObject().Call<AndroidJavaObject>("getAndroidUnityBridge");
                    Debug.Log("============== Java Android Unity Bridge initialized.");
                }
                else
                {
                    Debug.Log("============== Java Android Unity Bridge already initialized.");
                }
            }
            else
            {
                javaAndroidUnityBridge = null;
                Debug.Log("============== Java Android Unity Bridge  -- current platform != Android.");
            }
        }

        public bool RequestToPlatform(IRequest request, Action<string> callback)
        {
            // InitJavaAndroidUnityBridgeReference();

            if (javaAndroidUnityBridge != null)
                return javaAndroidUnityBridge.Call<bool>("requestCallbackToAndroid", request.ToJson(), new InternalIAndroidUnityCallback(callback));
            else
                return false;
        }

        public void RespondToPlatform(long id, string jsonResult)
        {
            //InitJavaAndroidUnityBridgeReference();

            if (javaAndroidUnityBridge != null)
                javaAndroidUnityBridge.Call("respondCallbackToAndroid", id, jsonResult);
        }

        public bool SendSingleMessageToPlatform(string jsonMessage)
        {
            // InitJavaAndroidUnityBridgeReference();

            return javaAndroidUnityBridge != null && javaAndroidUnityBridge.Call<bool>("sendSingleMessageToAndroid", jsonMessage);
        }

    }
}
