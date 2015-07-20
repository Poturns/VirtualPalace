using UnityEngine;

namespace AndroidApi
{
	internal class MessageListener : AndroidJavaProxy
	{
		private IWearableMessageListener listener;
		internal MessageListener (IWearableMessageListener listener) : base(Utils.MessageListenerClassName)
		{
			this.listener = listener;
		}

		public void onMessageReceived(string path, AndroidJavaObject obj)
		{
			listener.OnMessageReceived (path, obj);
		}

		public void onMessageReceived(string path, string str)
		{
			listener.OnMessageReceived (path, str);
		}
	}
}

