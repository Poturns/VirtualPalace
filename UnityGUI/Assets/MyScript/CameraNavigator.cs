using BridgeApi.Controller;
using UnityEngine;

namespace MyScript
{
    /// <summary>
    /// Direction을 이용하여 Camera(Player)를 이동시키는 역할을 수행하는 클래스
    /// </summary>
    public class CameraNavigator
    {
        /// <summary>
        /// 주어진 direction에 적절하게 player를 이동시킨다.
        /// </summary>
        /// <param name="player">이동할 player</param>
        /// <param name="direction">이동할 방향</param>
        /// <returns>이동이 처리되었는지 여부</returns>
        public static bool MoveCamera(GameObject player, Direction direction)
        {
            if (player == null)
            {
                Debug.Log("Player == null");
                return false;
            }

            Debug.Log("Move to : " + direction.Value);
            Vector3 NewVector;

            switch (direction.Value)
            {
                case Direction.EAST:
                    NewVector = Camera.main.transform.right;
                    break;
                case Direction.WEST:
                    NewVector = -Camera.main.transform.right;
                    break;
                case Direction.SOUTH:
                    NewVector = -Camera.main.transform.forward;
                    NewVector.y = 0;
                    break;
                case Direction.NORTH:
                    NewVector = Camera.main.transform.forward;
                    NewVector.y = 0;
                    break;
                default:
                    return false;
            }

            if (player.transform != null)
            {
                /* * direction.Amount*/
				player.GetComponent<CharacterController>().Move(Time.deltaTime *NewVector);
				return true;
            }
            else
            {
                Debug.LogError("Player.transform == null");
                return false;
            }

        }


    }
}
