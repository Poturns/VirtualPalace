using System;
using UnityEngine;

namespace AndroidApi
{
	public class Utils
	{
		public const string UnityActivityClassName = "kr.poturns.util.VPUnityActivity";

		public const string SpeechToTextListenerClassName = "kr.poturns.util.SpeechToTextHelper.STTListener";
		public const string MessageListenerClassName = "kr.poturns.util.WearableCommHelper.MessageListener";


		public static AndroidJavaObject GetActivityObject()
		{
			AndroidJavaClass playerClass = new AndroidJavaClass(UnityActivityClassName);
			AndroidJavaObject activity = playerClass.GetStatic<AndroidJavaObject>("currentActivity");
			
			return activity;
		}

		public static InputHandleHelperProxy GetInputHandleHelperProxy(){
			return new InputHandleHelperProxy (GetActivityObject());
		}
	}
}