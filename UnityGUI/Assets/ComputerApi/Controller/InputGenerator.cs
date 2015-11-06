
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
            else if (Input.GetKeyUp(KeyCode.K))
            {
                manager.HandleInputsFromController("{\"256\":3500011}");
            }
            else if (Input.GetKeyUp(KeyCode.I))
            {
                manager.HandleInputsFromController("{\"256\":7500011}");
            }
            else if (Input.GetKeyUp(KeyCode.J))
            {
                manager.HandleInputsFromController("{\"256\":5300011}");
            }
            else if (Input.GetKeyUp(KeyCode.L))
            {
               manager.HandleInputsFromController("{\"256\":5700011}");
            }

        }
    }
}
