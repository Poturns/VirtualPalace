using UnityEngine;
using System.Collections;
using MyScript.States;
using MyScript.Interface;

public class PictureObj : MonoBehaviour, IObject {

	
	public string Title {
		get;
		set;
	}
	private Texture2D Tex;
	private string Path;


	public void OnSelect()
	{
		StateManager.GetManager().SwitchState (new VRImageObjView(StateManager.GetManager(),gameObject));
	}
	// Use this for initialization
	void Start () {
		Title = "Test Obj";
	}
	public void PictureUpdate()
	{
		GetComponent<Renderer>().materials[1].mainTexture = Utils.Image.Load (Path);
	}
	// Update is called once per frame
	void Update () {
		
	}
}
