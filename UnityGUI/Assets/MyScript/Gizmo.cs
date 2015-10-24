using UnityEngine;
using System.Collections;

public class Gizmo : MonoBehaviour {
	public float Size = 0.1f;
	public Color Col = Color.yellow;
	void OnDrawGizmos()
	{
		Gizmos.color = Col;
		Gizmos.DrawWireSphere (transform.position, Size);
	}
}
