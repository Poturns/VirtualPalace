using UnityEngine;
using MyScript.States;

public class PictureObj : MonoBehaviour{

	
	public string Title {
		get;
		set;
	}
	public bool TextureUpdateOn = false;


    private string path;


	public string Path
	{
		set
		{
			path = value;
			if(TextureUpdateOn) PictureUpdate();
		}
		get
		{
			return path;
		}
	}

    private Renderer imageRenderer;

    public void OnSelect()
	{
		StateManager.GetManager().SwitchState (new VRImageObjView(StateManager.GetManager(),gameObject));
	}
	// Use this for initialization
	void Start () {
		Title = "Image Obj";
        imageRenderer = GetComponent<Renderer>();
    }

    public void PictureUpdate(string path)
    {
        this.path = path;
        PictureUpdate();
    }

    public void PictureUpdate()
	{
		if (!TextureUpdateOn)
			return;

		int Length = imageRenderer.materials.GetLength (0);
        imageRenderer.materials[Length - 1].mainTexture = Utils.Image.Load (Path);

	}
	// Update is called once per frame
	void Update () {

	}
}
