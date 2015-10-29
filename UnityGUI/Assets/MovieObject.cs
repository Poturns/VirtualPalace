using UnityEngine;
using System.Collections;

public class MovieObject : MonoBehaviour {

	public string Title {
		get;
		set;
	}
	private Texture2D FirstFrameTex;
	private string Path;
	
	
	public void OnSelect()
	{
		//StateManager.GetManager().SwitchState (new VRImageObjView(StateManager.GetManager(),gameObject));
	}
	// Use this for initialization
	void Start () {
		//Title = "Image Obj";
	}
	public void PlayMovie()
	{
		if (Path == null) 
		{
			Debug.Log ("Path is Null");
			return;
		}
		Handheld.PlayFullScreenMovie (Path);
	}
	// Update is called once per frame
	void Update () {
		
	}
}
