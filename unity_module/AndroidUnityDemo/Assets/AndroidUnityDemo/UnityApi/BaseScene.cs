using UnityEngine;
using UnityEngine.UI;
using System;
using System.Collections;
using System.Collections.Generic;
using AndroidApi;
using UnityApi;

public class BaseScene : MonoBehaviour
{
	AsyncTasker asyncTasker = new AsyncTasker ();
	protected InputHandleHelperProxy inputHandleHelperProxy;

	protected void Init ()
	{
		AndroidUtils.SetOnBackPressListener (() => {
			QueueOnMainThread (() => {
				Application.LoadLevel ("main_scene");});
			return true;
		});

		inputHandleHelperProxy = AndroidUtils.GetInputHandleHelperProxy ();
	}

	protected void OnUpdate ()
	{
		asyncTasker.OnUpdate ();
	}

	protected void QueueOnMainThread (Action action)
	{
		asyncTasker.QueueOnMainThread (action);
	}

	protected void RunAsync (Action action)
	{
		asyncTasker.RunAsync (action);
	}

}
