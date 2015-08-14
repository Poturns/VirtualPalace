using System;
using UnityEngine;

namespace AndroidApi.Drive
{
	public class DriveFolder : DriveResource
	{
		internal DriveFolder (AndroidJavaObject androidObject) : base(androidObject)
		{
		}

		public DriveFile CreateFile(DriveHandler handler, string title, string mimeType, DriveContents contents){
			return new DriveFile (handler.GetAndroidJavaObject().Call<AndroidJavaObject>("createFile", androidObject, title, mimeType, contents.androidObject));
		}

		public DriveFolder CreateFolder(DriveHandler handler, string title){
			return new DriveFolder (handler.GetAndroidJavaObject().Call<AndroidJavaObject>("createFolder", androidObject, title));
		}

		
		public MetadataBuffer ListChildren(DriveHandler handler) {
			return new MetadataBuffer (handler.GetAndroidJavaObject().Call<AndroidJavaObject>("listChildren", androidObject));
		}

		public MetadataBuffer QueryChildrenByTitle(DriveHandler handler, String title) {
			return new MetadataBuffer (handler.GetAndroidJavaObject().Call<AndroidJavaObject>("queryChildrenByTitle", androidObject, title));
		}

		public MetadataBuffer QueryChildrenByMimeType(DriveHandler handler, String mimeType) {
			return new MetadataBuffer (handler.GetAndroidJavaObject().Call<AndroidJavaObject>("queryChildrenByMimeType", androidObject, mimeType));
		}

	}
}

