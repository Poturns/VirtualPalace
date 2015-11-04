using UnityEngine;
using System.Collections.Generic;
using BridgeApi.Controller;

namespace MyScript.States
{
    public class VRMovieSelect : AbstractGazeInputState
    {
        private GameObject Target;

        private MovieControl MovieUI;
        private MovieObject movieObject;

        private MeshCollider movieUiMeshCollider;
        private MeshRenderer movieUiMeshRenderer;


        public VRMovieSelect(StateManager managerRef, GameObject TargetObject) : base(managerRef, "VRMovieSelect")
        {
            Target = TargetObject;

            DestroyMarkedObject();

            MovieUI = GameObject.Find("MovieFirstFrameView").GetComponent<MovieControl>();
            if (!MovieUI)
                Debug.Log("MovieSelector is Null");

            // StartIndex(선택 되있는거 부터 시작하도록)

            movieUiMeshCollider = MovieUI.gameObject.GetComponent<MeshCollider>();
            movieUiMeshRenderer = MovieUI.gameObject.GetComponent<MeshRenderer>();


            LockCameraAndMesh(true);

            movieObject = Target.GetComponent<MovieObject>();
            MovieUI.SetIndex(movieObject.IndexMovie);
            MovieUI.UpdateMovie();
        }

        protected override void HandleCancelOperation()
        {
            //base.HandleCancelOperation();
            ExitMovieState();
        }

        protected override void HandleDirectionOperation(Dictionary<int, Direction> directionDictionary)
        {
            //base.HandleDirectionOperation(directionDictionary);

            foreach (int key in directionDictionary.Keys)
            {
                //ChooseMovie(directionDictionary[key])
                // 명령이 여러번 오는 경우 썸네일 로딩 낭비를 막기 위하여 한번만 썸네일 변경을 실시한다.
                if (ChooseMovie(directionDictionary[key]))
                    return;
            }
        }

        private bool ChooseMovie(Direction direction)
        {
            switch (direction.Value)
            {
                case Direction.EAST:
                    MovieUI.ShowNextMovieThumbnail();
                    break;
                case Direction.WEST:
                    MovieUI.ShowPrevMovieThumbnail();
                    break;
                default:
                    return false;
            }
            return true;
        }

        void ExitMovieState()
        {
            Debug.Log("=============== Exit VRMovieSelect");

            LockCameraAndMesh(false);

            movieObject.IndexMovie = MovieUI.GetIndex();
			movieObject.Path = MovieUI.MoviePath;
            SwitchState(new VRSceneIdleState(Manager));
        }

        private void LockCameraAndMesh(bool isLock)
        {
            ChangeMeshState(isLock);
            SetGazeInputMode(isLock ? GAZE_MODE.OFF : GAZE_MODE.OBJECT);
            SetCameraLock(isLock);
        }

        private void ChangeMeshState(bool enable)
        {
            movieUiMeshRenderer.enabled = enable;
            movieUiMeshCollider.enabled = enable;
        }

    }
}

