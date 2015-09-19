﻿using System;
using UnityEngine;

namespace AndroidApi
{
	public sealed class AndroidUtils
	{
		public const string UnityPlayerClassName = "com.unity3d.player.UnityPlayer";
		public const string RunOnUiThreadMethodName = "runOnUiThread";

		private AndroidUtils ()
		{
		}

		public static AndroidJavaObject GetActivityObject ()
		{
			using (AndroidJavaClass playerClass = new AndroidJavaClass(UnityPlayerClassName)) {
				AndroidJavaObject activity = playerClass.GetStatic<AndroidJavaObject> ("currentActivity");
				return activity;
			}
		}

	}
}