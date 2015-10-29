using UnityEngine;
using System.Collections;
using MyScript.States;

public class MovieObject : MonoBehaviour {

	public string Title {
		get;
		set;
	}
	private Texture2D FirstFrameTex;
	public int IndexMovie = 0;
	
	
	public void OnSelect()
	{
		StateManager.GetManager().SwitchState (new VRMovieSelect(StateManager.GetManager(),gameObject));
	}
	// Use this for initialization
	void Start () {
		//Title = "Image Obj";
	}

}
