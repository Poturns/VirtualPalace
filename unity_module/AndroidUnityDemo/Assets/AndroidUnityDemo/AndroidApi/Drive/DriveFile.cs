using UnityEngine;

namespace AndroidApi.Drive
{
    public class DriveFile: DriveResource
	{
		public const int MODE_READ_ONLY = 268435456;
		public const int MODE_READ_WRITE = 805306368;
		public const int MODE_WRITE_ONLY = 536870912;

		internal DriveFile (AndroidJavaObject androidObject) : base(androidObject)
		{
		}

		public DriveContents OpenFile (DriveHandler handler, int mode)
		{
			return new DriveContents (handler.GetAndroidJavaObject ().Call<AndroidJavaObject> ("openFile", androidObject, mode));
		}
		
		public Status DeleteFile (DriveHandler handler)
		{
			return new Status (handler.GetAndroidJavaObject ().Call<AndroidJavaObject> ("deleteFile", androidObject));
		}

	}
}

