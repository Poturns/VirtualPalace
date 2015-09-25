using AndroidApi.Controller.Request;
using System;
using System.Runtime.CompilerServices;
using UnityEngine;

namespace AndroidApi.Controller
{
    /// <summary>
    /// ANDROID - UNITY 간 통신 클래스
    /// </summary>
    public class AndroidUnityBridge
    {
        private static AndroidUnityBridge sInstance;

        /// <summary>
        /// ANDROID - UNITY 간 통신 객체를 반환한다.
        /// </summary>
        /// <returns>ANDROID - UNITY 간 통신 객체</returns>
        [MethodImpl(MethodImplOptions.Synchronized)]
        public static AndroidUnityBridge GetInstance()
        {
            if (sInstance == null)
            {
                sInstance = new AndroidUnityBridge();
            }

            return sInstance;
        }


        /// <summary>
        /// java 형태의 AndroidUnityBridge 객체
        /// </summary>
        private AndroidJavaObject javaAndroidUnityBridge;
		/// <summary>
		/// Single 메시지를 전달받는 event
		/// </summary>
        public event Action<string> OnMessageReceived;
		/// <summary>
		/// Input 메시지를 전달받는 event
		/// </summary>
        public event Action<Controller.Operation[]> OnInputReceived;



        private AndroidUnityBridge()
        {
            javaAndroidUnityBridge = AndroidUtils.GetActivityObject().Call<AndroidJavaObject>("getAndroidUnityBridge");
            javaAndroidUnityBridge.Call("setMessageCallback", new InternalIAndroidUnityCallback(OnMessageCallback));
            javaAndroidUnityBridge.Call("setInputCallback", new InternalIAndroidUnityCallback(OnInputCallback));
        }

        /// <summary>
        /// java AndroidUnityBridge에서 Input이 전달될 때 호출된다.
        /// </summary>
        /// <param name="json">Input이 기술된 Json</param>
        [MethodImpl(MethodImplOptions.Synchronized)]
        private void OnInputCallback(string json)
        {
            if (OnInputReceived != null)
                OnInputReceived(JsonInterpreter.InterpretInputCommands(json));
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        private void OnMessageCallback(string json)
        {
            if (OnMessageReceived != null)
                OnMessageReceived(json);
        }

        /// <summary>
        /// UNITY에서 ANDROID 에 요청을 보낸다.
        /// </summary>
        /// <param name="jsonMessage">요청의 세부 사항이 Json형태로 기술되어 있는 문자열</param>
        /// <param name="callback">요청에 대한 응답을 받을 콜백</param>
        /// <returns>요청이 접수되었을 경우, TRUE</returns>
        public bool RequestToAndroid(IRequest request, Action<Controller.RequestResult> callback)
        {
            return javaAndroidUnityBridge.Call<bool>("requestCallbackToAndroid", request.ToJson(),
                new InternalIAndroidUnityCallback((json) => callback(JsonInterpreter.InterpretResultFromAndroid(json)))
            );
        }

        /// <summary>
        /// ANDROID 에서 요청한 ID 에 해당하는 결과를 콜백메소드로 반환한다.
        /// </summary>
        /// <param name="id">콜백의 id</param>
        /// <param name="jsonResult">요청에 대한 결과값이 Json형태로 기술된 문자열</param>
        public void RespondToAndroid(long id, string jsonResult)
        {
            javaAndroidUnityBridge.Call("respondCallbackToAndroid", id, jsonResult);
        }

        /// <summary>
        /// UNITY 에서 단일 메시지를 ANDROID 로 전송한다.
        /// </summary>
        /// <param name="jsonMessage">전송할 Json 메시지</param>
        /// <returns>메시지가 정상적으로 전송되었을 때, TRUE</returns>
        public bool SendSingleMessageToAndroid(string jsonMessage)
        {
            return javaAndroidUnityBridge.Call<bool>("sendSingleMessageToAndroid", jsonMessage);
        }


    }

}

