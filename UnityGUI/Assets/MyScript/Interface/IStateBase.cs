using System.Collections.Generic;
using BridgeApi.Controller;

namespace MyScript.Interface
{
    /// <summary>
    /// VR, AR 등의 현재 화면 상태를 나타내는 클래스
    /// </summary>
	public interface IStateBase
	{
        /// <summary>
        /// 화면 frame이 update 될 때마다 실행된다.
        /// </summary>
		void StateUpdate();

        /// <summary>
        /// GUI가 그려질 때 실행된다.
        /// </summary>
		void ShowIt();

        /// <summary>
        /// InputModule에서 전달 된 Input을 처리한다.
        /// </summary>
        /// <param name="InputOp">InputModule에서 전달 된 Input 리스트</param>
		void InputHandling(List<Operation> InputOp);
	}

}
