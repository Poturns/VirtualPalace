using UnityEngine;
using System.Collections;
using BridgeApi.Controller;


public class ARRenderingObject : MonoBehaviour {
	//카메라 근평면 으로 부터의 거리
	public float zOffset;
	public ARrenderItem ARItem;
	void Start () 
	{
		zOffset = GameObject.Find ("ARView").transform.position.z;
	}
	public void SetARItem(ARrenderItem item)
	{
		ARItem.screenX = item.screenX;
		ARItem.screenY= item.screenY;
		ARItem.resId = item.resId;
	}
	public void SetARPosition(ARrenderItem item)
	{
		//위치를 Screen좌표기준으로 변경
		transform.position =Camera.main.ScreenToWorldPoint (new Vector3 (item.screenX,
		                                                                 item.screenY, 
		                                                                 Camera.main.nearClipPlane+zOffset));

	}

}
