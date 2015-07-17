using System;
using UnityEngine;

namespace AndroidApi
{
	public abstract class IInputHandleHelper<Listener>
	{
		internal AndroidJavaObject activity, inputHandleHelper;

		internal IInputHandleHelper (AndroidJavaObject activity, AndroidJavaObject inputHandleHelper)
		{
			this.activity = activity;
			this.inputHandleHelper = inputHandleHelper;
		}

		public void Start()
		{
			activity.Call(Utils.RunOnUiThreadMethodName, new AndroidJavaRunnable( () => inputHandleHelper.Call ("start") ));
		}

		public void Stop()
		{
			activity.Call (Utils.RunOnUiThreadMethodName, new AndroidJavaRunnable ( () => inputHandleHelper.Call ("stop") ));
		}

		public void Dispose()
		{
			inputHandleHelper.Dispose ();
		}

		public abstract void SetListener(Listener listener);


	}
}

