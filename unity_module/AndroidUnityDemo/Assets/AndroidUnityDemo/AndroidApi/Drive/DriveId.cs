using System;
using UnityEngine;

namespace AndroidApi.Drive
{
	public class DriveId : AndroidApi.IAndroidObject
	{
		internal DriveId (AndroidJavaObject androidObject) : base(androidObject)
		{
		}

		public string GetStringForm ()
		{
			return ToString ();
		}

		public override string ToString ()
		{
			return androidObject.Call<string> ("toString");
		}

		public static DriveId DecodeFromString (string s)
		{
			using (AndroidJavaClass clazz = new AndroidJavaClass("com.google.android.gms.drive.DriveId")) {
				return new DriveId (clazz.CallStatic<AndroidJavaObject> ("decodeFromString", s));
			}
		}
	}
}

