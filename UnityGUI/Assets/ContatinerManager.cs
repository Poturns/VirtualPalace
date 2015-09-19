using UnityEngine;
using System.Collections;
using System.Collections.Generic;

public class ContatinerManager : MonoBehaviour 
{
	private List<GameObject> ContainerList;

	void Start()
	{
		ContainerList = new List<GameObject>();
		GameObject[] Objs = GameObject.FindGameObjectsWithTag("BookCaseTrigger");

		Debug.Log (Objs.GetLength (0));
		foreach(GameObject obj in Objs)
		{
			ContainerList.Add(obj);
		}
	}

}
