using UnityEngine;
using System.Collections.Generic;
using BridgeApi.Controller;

namespace MyScript.States
{
	public class VRMovieSelect : AbstractGazeInputState
	{
		private MovieControl MovieUI;
		private GameObject Target;
		
		public VRMovieSelect(StateManager managerRef, GameObject TargetObject) : base(managerRef, "VRMovieSelect")
		{
			Target = TargetObject;
			
			GameObject DisposolObj = GameObject.FindGameObjectWithTag("Disposol");
			if (DisposolObj) GameObject.Destroy(DisposolObj);
			
			MovieUI = GameObject.Find("MovieFirstFrameView").GetComponent<MovieControl>();
			if (!MovieUI)
				Debug.Log("MovieSelector is Null");

			// StartIndex(선택 되있는거 부터 시작하도록)

			MovieUI.gameObject.GetComponent<MeshCollider>().enabled = true;
			MovieUI.gameObject.GetComponent<MeshRenderer>().enabled = true;

			MovieUI.SetIndex (Target.GetComponent<MovieObject> ().IndexMovie);
			MovieUI.UpdateMovie ();
		}
		
		protected override void HandleCancelOperation()
		{
			base.HandleCancelOperation();
			ExitMovieState();
		}
		protected override void HandleDirectionOperation(Dictionary<int, Direction> directionDictionary)
		{
			base.HandleDirectionOperation (directionDictionary);

			foreach (int key in directionDictionary.Keys)
			{

				ChooseMovie(directionDictionary[key]);
					
			}
		}
		private bool ChooseMovie(Direction direction)
		{
			switch (direction.Value)
			{
			case Direction.EAST:
				MovieUI.ChangeMovie(1);
				break;
			case Direction.WEST:
				MovieUI.ChangeMovie(-1);
				break;
			default:
				return false;
			}
			return true;
		}
		void ExitMovieState()
		{
			Debug.Log("Exit MovieState");
			MovieUI.gameObject.GetComponent<MeshCollider>().enabled = false;
			MovieUI.gameObject.GetComponent<MeshRenderer>().enabled = false;
			Target.GetComponent<MovieObject> ().IndexMovie = MovieUI.GetIndex ();
			SwitchState(new VRSceneIdleState(Manager));
		}
		
	}
}

