using UnityEngine;

namespace AndroidApi
{
	public interface IWearableMessageListener
	{
		void OnMessageReceived(string path, AndroidJavaObject data);

		void OnMessageReceived(string path, string str);
	}
}