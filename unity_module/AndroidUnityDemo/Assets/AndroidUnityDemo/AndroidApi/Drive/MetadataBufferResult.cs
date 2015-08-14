using System;
using UnityEngine;

namespace AndroidApi.Drive
{
	public class MetadataBufferResult : IAndroidObject
	{
		internal MetadataBufferResult (AndroidJavaObject androidObject) : base(androidObject)
		{
		}

		public Status GetStatus ()
		{
			return new Status (androidObject.Call<AndroidJavaObject> ("getStatus"));
		}

		public void Release ()
		{
			androidObject.Call ("release");
		}

		public MetadataBuffer GetMetadataBuffer ()
		{
			return new MetadataBuffer (androidObject.Call<AndroidJavaObject> ("getMetadataBuffer"));
		}
	}
}

