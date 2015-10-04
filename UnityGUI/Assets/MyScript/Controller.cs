using UnityEngine;
using System.Collections;

[RequireComponent(typeof(CharacterController))]
public class Controller : MonoBehaviour {

	public float MoveSpeed;
	CharacterController cc;
	// Use this for initialization
	void Start () 
	{
		cc = GetComponent<CharacterController> ();
	}
	
	// Update is called once per frame
	void Update () 
	{
		Vector3 forword = Input.GetAxis ("Vertical") * transform.TransformDirection (Vector3.forward) * MoveSpeed;
		Vector3 VecHori = Input.GetAxis ("Horizontal") * transform.TransformDirection (Vector3.right) * MoveSpeed;
		cc.Move (forword * Time.deltaTime);
		cc.Move (VecHori * Time.deltaTime);
		//cc.SimpleMove (Physics.gravity);
	}
}
