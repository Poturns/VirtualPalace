using UnityEngine;

namespace AndroidApi.Drive
{
    public class DriveHandler : IInputHandleHelper<short>
	{
		internal DriveHandler (AndroidJavaObject activity, AndroidJavaObject inputHandleHelper) : base(activity, inputHandleHelper)
		{
		}

		public override void SetListener (short listener)
		{
		}

		internal AndroidJavaObject GetAndroidJavaObject ()
		{
			return inputHandleHelper;
		}

		public DriveContents OpenFile (DriveId id, int mode)
		{
			return new DriveContents (inputHandleHelper.Call<AndroidJavaObject> ("openFile", id.androidObject, mode));
		}
		
		public DriveFile GetFile (DriveId id)
		{
			return new DriveFile (inputHandleHelper.Call<AndroidJavaObject> ("getFile", id.androidObject));
		}
		
		public DriveFolder GetRootFolder ()
		{
			return new DriveFolder (inputHandleHelper.Call<AndroidJavaObject> ("getRootFolder"));
		}

		public DriveFolder getFolder (DriveId id)
		{
			return new DriveFolder (inputHandleHelper.Call<AndroidJavaObject> ("getFolder", id.androidObject));
		}
		
		public DriveFolder GetAppFolder ()
		{
			return new DriveFolder (inputHandleHelper.Call<AndroidJavaObject> ("getAppFolder"));
		}
		
		public DriveContents NewDriveContents ()
		{
			return new DriveContents (inputHandleHelper.Call<AndroidJavaObject> ("newDriveContents"));
		}
		
		public MetadataBufferResult QueryByTitle (string title)
		{
			return new MetadataBufferResult (inputHandleHelper.Call<AndroidJavaObject> ("queryByTitle", title));
		}

		public MetadataBufferResult QueryByMimeType (string mimeType)
		{
			return new MetadataBufferResult (inputHandleHelper.Call<AndroidJavaObject> ("queryByMimeType", mimeType));
		}
	}
}

