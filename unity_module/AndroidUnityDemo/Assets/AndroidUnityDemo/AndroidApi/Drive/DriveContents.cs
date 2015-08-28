using System;
using UnityEngine;

namespace AndroidApi.Drive
{
	public class DriveContents : IAndroidObject
    {
		internal DriveContents (AndroidJavaObject androidObject) : base(androidObject)
		{
		}

		public DriveId GetDriveId ()
		{
			return new DriveId (androidObject.Call<AndroidJavaObject> ("getDriveId"));
		}

		public int GetMode ()
		{
			return androidObject.Call<int> ("getMode");
		}

		public Status Commit (DriveHandler handler)
		{
			return new Status (handler.GetAndroidJavaObject ().Call<AndroidJavaObject> ("commit", androidObject));
		}

		public Status Commit (DriveHandler handler, string title)
		{
			return new Status (handler.GetAndroidJavaObject ().Call<AndroidJavaObject> ("commit", androidObject, title));
		}

		public void Discard (DriveHandler handler)
		{
			handler.GetAndroidJavaObject ().Call ("discard");
		}

		public byte[] OpenContents (DriveHandler handler)
		{
			string str = OpenContentsToString (handler);
			if (str == null)
				return null;

			byte[] bytes = new byte[str.Length * sizeof(char)];
            Buffer.BlockCopy (str.ToCharArray (), 0, bytes, 0, bytes.Length);
			return bytes;
		}

		public string OpenContentsToString (DriveHandler handler)
		{
			return handler.GetAndroidJavaObject ().CallStatic<string> ("openContents", androidObject);
		}

		public string OpenContentsToString (DriveHandler handler, string encoding)
		{
			return handler.GetAndroidJavaObject ().CallStatic<string> ("openContents", androidObject, encoding);
		}

		public bool WriteContents (DriveHandler handler, string str)
		{
			return handler.GetAndroidJavaObject ().CallStatic<bool> ("writeContents", androidObject, str); 
		}

		public bool WriteContents (DriveHandler handler, byte[] bytes)
		{
			char[] chars = new char[bytes.Length / sizeof(char)];
            Buffer.BlockCopy (bytes, 0, chars, 0, bytes.Length);
			return WriteContents (handler, new string (chars));
		}
	}
}

