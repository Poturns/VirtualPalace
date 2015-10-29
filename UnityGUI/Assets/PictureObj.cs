using UnityEngine;
using System.Collections;
using MyScript.States;
using MyScript.Interface;

public class PictureObj : MonoBehaviour{

	
	public string Title {
		get;
		set;
	}
	private Texture2D Tex;
	public bool TextureUpdateOn = false;
	private string Path;


	public void OnSelect()
	{
		StateManager.GetManager().SwitchState (new VRImageObjView(StateManager.GetManager(),gameObject));
	}
	// Use this for initialization
	void Start () {
		Title = "Image Obj";
	}
	public void SetPath(string TexPath)
	{
		Path = TexPath;
	}
	public void PictureUpdate()
	{
		if (!TextureUpdateOn)
			return;

		int Length = GetComponent<Renderer> ().materials.GetLength (0);
		GetComponent<Renderer>().materials[Length - 1].mainTexture = Utils.Image.Load (Path);

	}
	// Update is called once per frame
	void Update () {
		
	}
}
