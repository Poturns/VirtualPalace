﻿using UnityEngine;
using System.Collections;
using MyScript.Interface;
using MyScript;

/// <summary>
/// GazeCursor의 SelectMode
/// </summary>
public enum GAZE_MODE
{
    /// <summary>
    /// Interactive
    /// </summary>
	OBJECT,
    /// <summary>
    /// MoveUI
    /// </summary>
	UI,
    /// <summary>
    /// Disable Cursor
    /// </summary>
	OFF
};


[System.Serializable]
public class GazeCusor : MonoBehaviour {
    private bool gazeDisable = false;

	public float GazeTime = 0.0f;
	public bool	GazeSelete =false;
	public readonly float GazeTimeMax = 5.0f;
	public Texture TextTex;
	public Texture ImageTex;
	public Texture MovieTex;
	public Texture NormalTex;
	public AbstractBasicObject GazeObject {
		get;set;
	}
	public GAZE_MODE Mode;

	void OnEnable()
	{
        if (gazeDisable)
            return;


       // Debug.Log ("CursorEnalbe");
		GazeTime = 0.0f;
		GazeSelete = false;
	
	}
	/// <summary>
	/// Reset(Init) Gazing Time
	/// </summary>
	public void ResetGazingTime()
	{
		GazeTime = 0.0f;
		//Debug.Log ("--Reset Gazing Time-- GazeTime : " + GazeTime);
	}
	public void SetTextureCursor(KIND_SOURCE kind)
	{

		switch (kind) 
		{
		case KIND_SOURCE.TEXT:
			GetComponent<Renderer>().material.mainTexture = TextTex;
			break;
		case KIND_SOURCE.MOVIE:
			GetComponent<Renderer>().material.mainTexture = MovieTex;
			break;

		case KIND_SOURCE.IMAGE:
			GetComponent<Renderer>().material.mainTexture = ImageTex;
			break;
		case KIND_SOURCE.BOOK_CASE:
			GetComponent<Renderer>().material.mainTexture = NormalTex;
			break;
		case KIND_SOURCE.UI_BUTTON:
			GetComponent<Renderer>().material.mainTexture = NormalTex;
			break;
		default:
			GetComponent<Renderer>().material.mainTexture = NormalTex;
			break;
		}
	}
	public bool GazeModeCheck(AbstractBasicObject Obj)
	{
		GazeObject = Obj;

		switch (Mode) 
		{
		case GAZE_MODE.OBJECT:
			return GazeObject.CompareTag("InteractiveObject");
		case GAZE_MODE.UI:
			return GazeObject.CompareTag("2DMoveUI");
		case GAZE_MODE.OFF:
			return false;
		}

		return false;
	}
	void OnDisable()
	{
        if (gazeDisable)
            return;
       // Debug.Log ("CursorDisalbe");
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
        if (gazeDisable)
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
