using UnityEngine;

namespace AndroidApi.Drive
{
    public class DriveResource : IAndroidObject
	{
		internal DriveResource (AndroidJavaObject androidObject) : base(androidObject)
		{
		}

		public DriveId GetDriveId ()
		{
			return new DriveId (androidObject.Call<AndroidJavaObject> ("getDriveId"));
		}
	}
}

