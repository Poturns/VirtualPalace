
using BridgeApi.Controller;
using System.Text;
using UnityEngine;

namespace ComputerApi.Controller
{
    public class InputGenerator
    {

        public void StateUpdate()
        {
            StateManager manager = StateManager.GetManager();

            // cancel
            if (Input.GetKeyUp(KeyCode.C))
            {
                manager.HandleInputsFromController("{\"2\":1}");
            }
            // select
            else if (Input.GetKeyUp(KeyCode.G))
            {
                manager.HandleInputsFromController("{\"1\":1}");
            }
            //deep
            else if (Input.GetKeyUp(KeyCode.H))
            {
                manager.HandleInputsFromController("{\"4096\":1}");
            }
            //switch mode
            else if (Input.GetKeyUp(KeyCode.T))
            {
                manager.HandleInputsFromController("{\"32768\":0}");
            }
            // down
            else if (Input.GetKeyUp(KeyCode.K))
            {
                manager.HandleInputsFromController("{\"256\":3500011}");
            }
            // up
            else if (Input.GetKeyUp(KeyCode.I))
            {
                manager.HandleInputsFromController("{\"256\":7500011}");
            }
            // left
            else if (Input.GetKeyUp(KeyCode.J))
            {
                manager.HandleInputsFromController("{\"256\":5300011}");
            }
            //right
            else if (Input.GetKeyUp(KeyCode.L))
            {
                manager.HandleInputsFromController("{\"256\":5700011}");
            }
            // generate AR item
            else if (Input.GetKeyUp(KeyCode.N))
            {
                float random = Random.value;
                manager.HandleInputsFromController(random < 0.3 ? "{\"-1\":105980720}" :
                   random > 0.7 ? "{\"-1\":217941080}" : "{\"-1\":111960360}");
            }
            //generate Toast
            else if (Input.GetKeyUp(KeyCode.P))
            {
                manager.HandleMessageFromController(new StringBuilder()
                    .Append("{")
                    .AppendFormat("\"{0}\":", ControllerEvent.EVENT_TOAST_MESSAGE)
                        .Append("{")
                        .AppendFormat("\"{0}\":\"{1}\"", ToastMessage.KEY_TOAST_MESSAGE_TYPE, "success")
                        .Append(",")
                        .AppendFormat("\"{0}\":\"{1}\"", ToastMessage.KEY_TOAST_MESSAGE_MSG, "test 테스트 test 테스트")
                        .Append("}")
                    .Append("}")
                    .ToString());
            }

        }
    }
}
