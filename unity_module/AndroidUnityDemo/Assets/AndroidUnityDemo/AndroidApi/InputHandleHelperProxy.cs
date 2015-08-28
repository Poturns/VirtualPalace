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
			//Debug.Log (activity.Call<string>("toString"));
			inputHandlerProxy = activity.Call<AndroidJavaObject>("getInputHandleHelperProxy");
			inputHandlerProxy.Call ("createInputHandleHelperAll");
		}

		/*
		public IInputHandleHelper GetInputHandler(int which){
			return new IInputHandleHelper(inputHandlerProxy.Call<AndroidJavaObject>("createInputHandleHelper", activity, which));
		}
		*/


		public STTInputHandler GetSTTInputHandler()
		{
			return new STTInputHandler(activity, inputHandlerProxy.Call<AndroidJavaObject>("getInputHandleHelper", INPUT_HELPER_STT));
		}

		public WearableInputHandler GetWearableInputHandler()
		{
			return new WearableInputHandler (activity, inputHandlerProxy.Call<AndroidJavaObject> ("getInputHandleHelper", INPUT_HELPER_WEARABLE));
		}

		public Drive.DriveHandler GetDriveHandler()
		{
			return new Drive.DriveHandler(activity, inputHandlerProxy.Call<AndroidJavaObject> ("getInputHandleHelper", INPUT_HELPER_DRIVE));
		}

		public void Dispose()
		{
			activity.Dispose ();
		}

	}
}

