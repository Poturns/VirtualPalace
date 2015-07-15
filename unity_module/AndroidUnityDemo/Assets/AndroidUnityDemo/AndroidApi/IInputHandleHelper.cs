using System;
using UnityEngine;

namespace AndroidApi
{
	public abstract class IInputHandleHelper<Listener>
	{
		internal AndroidJavaObject inputHandleHelper;
		internal IInputHandleHelper (AndroidJavaObject inputHandleHelper)
		{
			this.inputHandleHelper = inputHandleHelper;
		}

		public void Start()
		{
			inputHandleHelper.Call ("start");
		}

		public void Stop()
		{
			inputHandleHelper.Call ("stop");
		}

		public abstract void SetListener(Listener listener);

	}
}

