using System;
using UnityEngine;

namespace AndroidApi
{
	public class WearableInputHandler : IInputHandleHelper<IWearableMessageListener>
	{
		internal WearableInputHandler (AndroidJavaObject inputHandleHelper) : base(inputHandleHelper)
		{
		}

		public override void SetListener(IWearableMessageListener listener)
		{
			MessageListener messageListener = new MessageListener (listener);
			inputHandleHelper.Call ("setMessageListener", messageListener);
		}
	}
}

