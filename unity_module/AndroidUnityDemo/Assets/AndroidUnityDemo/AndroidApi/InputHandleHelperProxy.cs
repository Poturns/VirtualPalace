using System;
using UnityEngine;

namespace AndroidApi
{
	public class InputHandleHelperProxy
	{
		public const int INPUT_HELPER_WEARABLE = 0;
		public const int INPUT_HELPER_STT = 1;
		public const int INPUT_HELPER_DRIVE = 2;

		private AndroidJavaObject activity, inputHandlerProxy;
		internal InputHandleHelperProxy (AndroidJavaObject activity)
		{
			this.activity = activity;
			inputHandlerProxy = activity.Call<AndroidJavaObject>("getInputHandleHelperProxy");

		}

		/*
		public IInputHandleHelper GetInputHandler(int which){
			return new IInputHandleHelper(inputHandlerProxy.Call<AndroidJavaObject>("createInputHandleHelper", activity, which));
		}
		*/


		public STTInputHandler GetSTTInputHandler(){
			return new STTInputHandler(inputHandlerProxy.Call<AndroidJavaObject>("createInputHandleHelper", activity, INPUT_HELPER_STT));
		}

		public WearableInputHandler GetWearableInputHandler(){
			return new WearableInputHandler (inputHandlerProxy.Call<AndroidJavaObject> ("createInputHandleHelper", activity, INPUT_HELPER_WEARABLE));
		}
	}
}

