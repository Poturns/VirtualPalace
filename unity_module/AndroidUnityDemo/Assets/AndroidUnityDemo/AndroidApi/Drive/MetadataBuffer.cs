using UnityEngine;

namespace AndroidApi.Drive
{
    public class MetadataBuffer : IAndroidObject
	{
		internal MetadataBuffer (AndroidJavaObject androidObject) : base(androidObject)
		{
		}

		public Metadata Get (int row)
		{
			return new Metadata (androidObject.Call<AndroidJavaObject> ("get", row));
		}

		public int GetCount ()
		{
			return androidObject.Call<int> ("getCount");
		}

		public void Release ()
		{
			androidObject.Call ("release");
		}
	}
}

