using LitJson;
using System;
using System.Collections.Generic;

namespace AndroidApi.Controller.Request
{
    /// <summary>
    /// Android에 상태를 통지하는 IRequest
    /// </summary>
    public class StateNotifyRequest : IRequest
    {
        /// <summary>
        /// 입력 장치
        /// </summary>
        public enum InputDevice
        {
            /*
            ScreenTouch = 0x1,
            ScreenFocus = 0x2,
            Motion = 0x4,
            */
            Voice = 0x10,
            Watch = 0x20
        }

        private const string SWITCH_PLAY_MODE = "switch_play_mode";
        private const string ACTIVATE_INPUT = "activate_input";
        private const string DEACTIVATE_INPUT = "deactivate_input";

        private JsonData jData = new JsonData();

        /// <summary>
        /// VirtualPalace 구동 환경을 변경한다.
        /// </summary>
        /// <param name="mode">변경할 구동 환경</param>
        public void SwitchPlayMode(VirtualPalacePlayMode mode)
        {
            jData[SWITCH_PLAY_MODE] = (int)mode;
        }

        /// <summary>
        /// InputDevice를 활성화 시킨다.
        /// </summary>
        /// <param name="device">활성화 시킬 InputDevice</param>
        public void ActivateInput(InputDevice device)
        {
            jData[ACTIVATE_INPUT] = (int)device;
        }

        /// <summary>
        /// InputDevice를 비활성화 시킨다.
        /// </summary>
        /// <param name="device">비활성화 시킬 InputDevice</param>
        public void DeactivateInput(InputDevice device)
        {
            jData[DEACTIVATE_INPUT] = (int)device;
        }

        public void SendRequest(Action<List<RequestResult>> callback)
        {
            AndroidUnityBridge.GetInstance().RequestToAndroid(this, (result) =>
            {
                callback(JsonInterpreter.ParseResultFromAndroid(result));
            });
        }

        public string ToJson()
        {
            return jData.ToJson();
        }

    }
}
