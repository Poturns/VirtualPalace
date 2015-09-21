using System;
using UnityEngine;

namespace AndroidApi
{
    /// <summary>
    /// Android에서의 AndroidUnityBridge$IAndroidUnityCallback 구현체
    /// </summary>
    internal class InternalIAndroidUnityCallback : AndroidJavaProxy
    {
        private const string I_ANDROID_UNITY_CALLBACK_NAME = "kr.poturns.virtualpalace.controller.AndroidUnityBridge$IAndroidUnityCallback";
        private Action<string> androidUnityCallback;

        internal InternalIAndroidUnityCallback(Action<string> iAndroidUnityCallback) : base(I_ANDROID_UNITY_CALLBACK_NAME)
        {
            SetCallback(iAndroidUnityCallback);
        }

        /// <summary>
        /// 콜백 결과를 전달받을 콜백을 설정한다.
        /// </summary>
        /// <param name="iAndroidUnityCallback">결과를 전달받을 콜백</param>
        public void SetCallback(Action<string> iAndroidUnityCallback)
        {
            androidUnityCallback = iAndroidUnityCallback;
        }

        /// <summary>
        /// AndroidUnityBridge$IAndroidUnityCallback의 메소드 'onCallback(String)'의 구현
        /// </summary>
        /// <param name="json">콜백 결과</param>
        public void onCallback(string json)
        {
            if (androidUnityCallback != null)
                androidUnityCallback(json);
        }
    }
}
