using UnityEngine;
using System.Collections;

public class AutoLoader : MonoBehaviour {


	//자동 게임 데이터 로드 객체
	// 첫번째 업데이트에서 데이터를 로드한후 오브젝트를 삭제한다.
	void Update () 
	{
		SaveLoader Saver = GameObject.Find ("_Script").GetComponent<SaveLoader> ();
		if (Saver == null)
			Debug.Log ("SaveLoaderFindFail");
		else 
		{
			Saver.LoadToFile ();			
			Debug.Log ("Load Complete");

		}
		Destroy (gameObject);
	}
}
