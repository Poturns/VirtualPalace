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
		Utils.SetOnBackPressListener (() => {
			QueueOnMainThread(() => {Application.LoadLevel ("main_scene");});
			return true;
		});

		inputHandleHelperProxy = Utils.GetInputHandleHelperProxy ();
	}

	protected void OnUpdate () 
	{
		asyncTasker.OnUpdate ();
	}

	protected void QueueOnMainThread(Action action)
	{
		asyncTasker.QueueOnMainThread (action);
	}

}
