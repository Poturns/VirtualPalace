using UnityEngine;
using System.Collections;

//AR에서 생성된 오브젝트를 버퍼링 하기 위한 ㅋ클래스 
public class ARObject : MonoBehaviour {
	public int ResID;
	public string Title;
	public string Resource;

	public ARObject()
	{}
	public ARObject(SaveData sData)
	{
		Resource = sData.Contents;
		Title = sData.ContentsTitle;
		ResID = sData.Key;
	}

}
