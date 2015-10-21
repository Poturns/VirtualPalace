using LitJson;
using System;
using System.Collections.Generic;
using System.Text;

namespace BridgeApi.Controller.Request
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

        private JsonWriter writer;
        private StringBuilder sb;

        public StateNotifyRequest()
        {
            writer = new JsonWriter(sb = new StringBuilder());
        }
        /// <summary>
        /// VirtualPalace 구동 환경을 변경한다.
        /// </summary>
        /// <param name="mode">변경할 구동 환경</param>
        public void SwitchPlayMode(VirtualPalacePlayMode mode)
        {
            writer.WriteObjectStart();
            writer.WritePropertyName(SWITCH_PLAY_MODE);
            writer.Write((int)mode);
            writer.WriteObjectEnd();
        }

        /// <summary>
        /// InputDevice를 활성화 시킨다.
        /// </summary>
        /// <param name="device">활성화 시킬 InputDevice</param>
        public void ActivateInput(InputDevice device)
        {
            writer.WriteObjectStart();
            writer.WritePropertyName(ACTIVATE_INPUT);
            writer.Write((int)device);
            writer.WriteObjectEnd();
        }

        /// <summary>
        /// InputDevice를 비활성화 시킨다.
        /// </summary>
        /// <param name="device">비활성화 시킬 InputDevice</param>
        public void DeactivateInput(InputDevice device)
        {
            writer.WriteObjectStart();
            writer.WritePropertyName(DEACTIVATE_INPUT);
            writer.Write((int)device);
            writer.WriteObjectEnd();
        }

        public void SendRequest(IPlatformBridge bridge, Action<List<RequestResult>> callback)
        {
            bridge.RequestToPlatform(this, (result) =>
            {
                callback(JsonInterpreter.ParseResultFromPlatform(result));
            });
        }

        public string ToJson()
        {
            return sb.ToString();
        }

    }
}
