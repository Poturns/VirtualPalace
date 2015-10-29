using UnityEngine;
using System.Collections;
using MyScript.Interface;

[System.Serializable]
public class GazeCusor : MonoBehaviour {

	public float GazeTime = 0.0f;
	public bool	GazeSelete =false;
	public float GazeTimeMax = 5.0f;
	public IRaycastedObject GazeObject {
		get;set;
	}
	void OnEnable()
	{
		Debug.Log ("CursorEnalbe");
		GazeTime = 0.0f;
		GazeSelete = false;
	
	}
	void OnDisable()
	{
		Debug.Log ("CursorDisalbe");
		GazeTime = 0.0f;
		GazeSelete = false;
		GazeObject = null;
	}
	void Update () 
	{

	//	if (GazeSelete) 
	//	{
	//		return;
	//	}
		return;
		GazeTime += Time.deltaTime;

		if (GazeTime > GazeTimeMax) 
		{
			if(GazeObject != null)
			{
				GazeObject.OnSelect();
				GazeTime = 0.0f;
				GazeObject = null;
	//			GazeSelete = false;
			}
	//		GazeSelete =true;
		}
			
		
	}
}
