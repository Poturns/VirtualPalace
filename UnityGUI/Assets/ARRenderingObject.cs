using UnityEngine;
using System.Collections;
using MyScript.Interface;
using BridgeApi.Controller;
using MyScript;


public class ARRenderingObject : AbstractBasicObject {
	//카메라 근평면 으로 부터의 거리
	public float zOffset;
	public ARrenderItem ARItem;
	public GameObject ARHead;

	public GameObject arView;
	void Start () 
	{
		arView = GameObject.Find ("ARView");
		ARHead = GameObject.Find ("Head");
		zOffset = arView.transform.position.z;
	}
	public void SetARItem(ARrenderItem item)
	{
		ARItem.screenX = item.screenX;
		ARItem.screenY= item.screenY;
		ARItem.resId = item.resId;
	}
	public override void OnSelect()
	{	
		arView.GetComponent<ARScreen> ().EndCamera ();
		StateManager.SwitchScene (UnityScene.VR);
	}
	//비율을 구한다음 PosDummy(parent)로부터의 상대 위치를 구해서 위치 변경
	public void SetARPosition(ARrenderItem item)
	{
		if (arView == null) 
		{
			arView = GameObject.Find("ARView");
		}
		if (ARHead == null) 
		{
			ARHead = GameObject.Find ("Head");
		}
		float Width = Screen.width;
		float Height = Screen.height;

		float XRatio = item.screenX / Width;
		float YRatio = item.screenY / Height;
		if (XRatio > 1)
			XRatio = 1;
		if (YRatio > 1)
			YRatio = 1;

		float RelativeX = XRatio * arView.transform.lossyScale.x;
		float RelativeY = YRatio * arView.transform.lossyScale.y;

	
		Vector3 HeadVector = ARHead.transform.forward;
		Vector3 MoveHeadVector = ARHead.transform.position + 0.01f*HeadVector;

		//HeadVector.Normalize ();
		Transform Parent = transform.parent;
		Debug.Log (" z :::::::::::" + Parent.position.z);
		//위치를 Screen좌표기준으로 변경
		transform.position =new Vector3 (Parent.position.x,
                                         Parent.position.y, 
		                                 Parent.position.z);
		transform.localPosition = new Vector3 (RelativeX*transform.lossyScale.x, RelativeY*transform.lossyScale.y, -0.01f);

	}

	void Update()
	{
		if(arView != null)
			gameObject.transform.rotation= arView.transform.rotation;
	}
}
