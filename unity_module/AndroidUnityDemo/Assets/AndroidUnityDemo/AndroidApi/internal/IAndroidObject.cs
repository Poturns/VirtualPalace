using UnityEngine;

namespace AndroidApi
{
    public class IAndroidObject
	{
		public AndroidJavaObject androidObject;

		protected IAndroidObject (AndroidJavaObject androidObject)
		{
			this.androidObject = androidObject;
		}
	}
}

