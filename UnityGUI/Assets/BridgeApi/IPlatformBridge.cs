using BridgeApi.Controller.Request;
using System;

namespace BridgeApi.Controller
{
    public interface IPlatformBridge : IPlatformBridgeDelegate
    {
        /// <summary>
        /// 기저 플랫폼의 Controller로부터 전달되어온 Input Message를 처리한다.
        /// </summary>
        /// <param name="json">Controller에서 전달된 Input Message json</param>
        void HandleInputsFromController(string json);


        /// <summary>
        /// 기저 플랫폼의 Controller로 부터 전달되어온 일반 Message를 처리한다.
        /// </summary>
        /// <param name="json">Controller에서 전달된 일반 Message json</param>
        void HandleMessageFromController(string json);

    }
}
