using System;
using UnityEngine;

namespace AndroidApi
{
	public class DriveHandler : IInputHandleHelper<Void>
	{
		internal DriveHandler (AndroidJavaObject activity, AndroidJavaObject inputHandleHelper) : base(activity, inputHandleHelper)
		{
		}

		public override void SetListener(Void listener)
		{
		}

		public void CreateDummyFileInAppFolder()
		{
		}

		public void QueryFileDummyFileInAppFolder()
		{

		}

		public void CommitFile()
		{

		}
	}

	internal class OnFileResultListener : AndroidJavaProxy
	{
		IFileResultListener listener;

		internal OnFileResultListener(IFileResultListener listener) : base("kr.poturns.util.DriveConnectionHelper$OnFileResultListener")
		{
			this.listener = listener;
		}

		public void onReceiveFileContent(AndroidJavaObject contents)
		{
			listener.OnReceiveFileContent (new DriveContents(contents));
		}
		
		public void onError(AndroidJavaObject status)
		{
			listener.OnError (new Status(status));
		}
	}

	public interface IFileResultListener
	{
		void OnReceiveFileContent(DriveContents contents);
		
		void OnError(Status status);
	}

	public class DriveContents
	{
		internal AndroidJavaObject driveContents;

		internal DriveContents(AndroidJavaObject driveContents)
		{
			this.driveContents = driveContents;
		}
	}

	public class Status
	{
		internal AndroidJavaObject status;

		internal Status(AndroidJavaObject status)
		{
			this.status = status;
		}

		public string GetStatusMessage()
		{
			return status.Call<string> ("getStatusMessage");
		}

		public bool IsSuccess()
		{
			return status.Call<bool> ("isSuccess");
		}
	}
}

