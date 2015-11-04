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
        /// 주어진 direction에 적절하게 시점을 이동시킨다.
        /// </summary>
        /// <param name="characterController">시점을 조정하는 객체</param>
        /// <param name="direction">이동할 방향</param>
        /// <returns>이동이 처리되었는지 여부</returns>
        public static bool MoveCamera(CharacterController characterController, Direction direction)
        {
            if (characterController == null)
            {
                Debug.Log("characterController == null");
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


            characterController.Move(Time.deltaTime * 5f * direction.Amount * NewVector);
            return true;


        }


    }
}
