using System;
using UnityEngine;

namespace AndroidApi
{
	public class OnBackPressListener : AndroidJavaProxy
	{
		Func<bool> listener;
		internal OnBackPressListener (Func<bool> listener) : base(AndroidUtils.OnBackPressListenerClassName)
		{
			this.listener = listener;
		}
		
		public bool onBackPressed()
		{
			return listener();
		}
	}
}

