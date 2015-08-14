using System;
using UnityEngine;

namespace AndroidApi.Drive
{
	public class Metadata: IAndroidObject
	{
		internal Metadata (AndroidJavaObject androidObject) : base(androidObject)
		{
		}

		public DriveId GetDriveId ()
		{
			return new DriveId (androidObject.Call<AndroidJavaObject> ("getDriveId"));
		}

		public string GetDescription ()
		{
			return androidObject.Call<string> ("getDescription");
		}

		public string GetEmbedLink ()
		{
			return androidObject.Call<string> ("getEmbedLink");
		}

		public string GetFileExtension ()
		{
			return androidObject.Call<string> ("getFileExtension");
		}
		
		public long GetFileSize ()
		{
			return androidObject.Call<long> ("getFileSize");
		}
		
		public string GetMimeType ()
		{
			return androidObject.Call<string> ("getMimeType");
		}
		
		public string GetOriginalFilename ()
		{
			return androidObject.Call<string> ("getOriginalFilename");
		}
		
		public bool IsPinnable ()
		{
			return androidObject.Call<bool> ("isPinnable");
		}
		
		public bool IsPinned ()
		{
			return androidObject.Call<bool> ("isPinned");
		}
		
		public long GetQuotaBytesUsed ()
		{
			return androidObject.Call<long> ("getQuotaBytesUsed");
		}
		
		public String GetTitle ()
		{
			return androidObject.Call<string> ("getTitle");
		}
		
		public String GetWebContentLink ()
		{
			return androidObject.Call<string> ("getWebContentLink");
		}
		
		public String GetWebViewLink ()
		{
			return androidObject.Call<string> ("getWebViewLink");
		}
		
		public bool IsInAppFolder ()
		{
			return androidObject.Call<bool> ("isInAppFolder");
		}
		
		public bool IsEditable ()
		{
			return androidObject.Call<bool> ("isEditable");
		}
		
		public bool IsFolder ()
		{
			return androidObject.Call<bool> ("isFolder");
		}
		
		public bool IsRestricted ()
		{
			return androidObject.Call<bool> ("isRestricted");
		}
		
		public bool IsShared ()
		{
			return androidObject.Call<bool> ("isShared");
		}
		
		public bool IsStarred ()
		{
			return androidObject.Call<bool> ("isStarred");
		}
		
		public bool IsTrashed ()
		{
			return androidObject.Call<bool> ("isTrashed");
		}
		
		public bool IsTrashable ()
		{
			return androidObject.Call<bool> ("isTrashable");
		}
		
		public bool IsExplicitlyTrashed ()
		{
			return androidObject.Call<bool> ("isExplicitlyTrashed");
		}
		
		public bool IsViewed ()
		{
			return androidObject.Call<bool> ("isViewed");
		}


		/*
		public Date getLastViewedByMeDate() {
			return (Date)this.zza(zzlq.zzahz);
		}

		public Date getModifiedByMeDate() {
			return (Date)this.zza(zzlq.zzahB);
		}
		
		public Date getModifiedDate() {
			return (Date)this.zza(zzlq.zzahA);
		}

		public Date getSharedWithMeDate ()
		{
		}

		*/
	}
}

