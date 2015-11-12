using UnityEngine;
using System.Collections;
using MyScript.Interface;
using BridgeApi.Controller;
using MyScript;


public class ARRenderingObject : AbstractBasicObject {
	//카메라 근평면 으로 부터의 거리
	public float zOffset;
	public ARrenderItem ARItem;
	public GameObject arView;
	void Start () 
	{
		arView = GameObject.Find ("ARView");
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
		Transform Parent = transform.parent;
		//위치를 Screen좌표기준으로 변경
		transform.position =new Vector3 (Parent.position.x + RelativeX,
                                         Parent.position.y + RelativeY, 
                                         Parent.position.z);

	}

}
