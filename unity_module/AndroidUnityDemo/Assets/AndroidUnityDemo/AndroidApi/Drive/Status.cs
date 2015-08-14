using System;
using UnityEngine;

namespace AndroidApi.Drive
{
	public class Status : IAndroidObject
	{
		internal Status (AndroidJavaObject androidObject) : base(androidObject)
		{
		}

		public bool IsSuccess ()
		{
			return androidObject.Call<bool> ("isSuccess");
		}
	}
}

