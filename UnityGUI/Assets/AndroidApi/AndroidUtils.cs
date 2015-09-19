using System;
using UnityEngine;

namespace AndroidApi
{
    /// <summary>
    /// 안드로이드 관런 작업 유틸리티 클래스
    /// </summary>
    public sealed class AndroidUtils
    {
        public const string UnityPlayerClassName = "com.unity3d.player.UnityPlayer";
        public const string RunOnUiThreadMethodName = "runOnUiThread";

        private AndroidUtils()
        {
        }

        /// <summary>
        /// Android에서의 Activity 객체를 얻는다.
        /// </summary>
        /// <returns>Activity 객체</returns>
        public static AndroidJavaObject GetActivityObject()
        {
            using (AndroidJavaClass playerClass = new AndroidJavaClass(UnityPlayerClassName))
            {
                AndroidJavaObject activity = playerClass.GetStatic<AndroidJavaObject>("currentActivity");
                return activity;
            }
        }

    }
}