using UnityEngine;

namespace AndroidApi
{
    public class WearableInputHandler : IInputHandleHelper<IWearableMessageListener>
	{
		internal WearableInputHandler (AndroidJavaObject activity, AndroidJavaObject inputHandleHelper) : base(activity, inputHandleHelper)
		{
		}

		public override void SetListener(IWearableMessageListener listener)
		{
			MessageListener messageListener = new MessageListener (listener);
			inputHandleHelper.Call ("setMessageListener", messageListener);
		}
	}
}

