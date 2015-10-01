using System.Collections.Generic;
using AndroidApi.Controller;

namespace MyScript.Interface
{
	public interface IStateBase
	{
		void StateUpdate();
		void ShowIt();
		void InputHandling(List<Operation> InputOp);
	}
}
