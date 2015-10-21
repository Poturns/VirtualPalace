using System.Collections.Generic;
using BridgeApi.Controller;

namespace MyScript.Interface
{
    /// <summary>
    /// VR, AR ���� ���� ȭ�� ���¸� ��Ÿ���� Ŭ����
    /// </summary>
	public interface IStateBase
	{
        /// <summary>
        /// ȭ�� frame�� update �� ������ ����ȴ�.
        /// </summary>
		void StateUpdate();

        /// <summary>
        /// GUI�� �׷��� �� ����ȴ�.
        /// </summary>
		void ShowIt();

        /// <summary>
        /// InputModule���� ���� �� Input�� ó���Ѵ�.
        /// </summary>
        /// <param name="InputOp">InputModule���� ���� �� Input ����Ʈ</param>
		void InputHandling(List<Operation> InputOp);
	}

}
