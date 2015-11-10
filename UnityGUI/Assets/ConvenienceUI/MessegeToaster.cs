using UnityEngine;
using System.Collections;

public class MessegeToaster : MonoBehaviour {

	private MeshRenderer renderer;
	private TextMesh ToasterText;
	void Start()
	{
		renderer = gameObject.GetComponent<MeshRenderer> ();
		ToasterText = gameObject.transform.GetChild (0).gameObject.GetComponent<TextMesh> ();
	}
	public void CallMessageToaster()
	{

	}
}