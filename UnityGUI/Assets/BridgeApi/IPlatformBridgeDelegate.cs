using BridgeApi.Controller.Request;
using System;

namespace BridgeApi.Controller
{
    public interface IPlatformBridgeDelegate
    {
        /// <summary>
        /// UNITY에서 기저 Platform에 요청을 보낸다.
        /// </summary>
        /// <param name="jsonMessage">요청의 세부 사항이 Json형태로 기술되어 있는 문자열</param>
        /// <param name="callback">요청에 대한 응답을 받을 콜백</param>
        /// <returns>요청이 접수되었을 경우, TRUE</returns>
        bool RequestToPlatform(IRequest request, Action<string> callback);

        /// <summary>
        /// 기저 Platform에서 요청한 ID 에 해당하는 결과를 콜백메소드로 반환한다.
        /// </summary>
        /// <param name="id">콜백의 id</param>
        /// <param name="jsonResult">요청에 대한 결과값이 Json형태로 기술된 문자열</param>
        void RespondToPlatform(long id, string jsonResult);

        /// <summary>
        /// UNITY 에서 단일 메시지를 기저 Platform으로 전송한다.
        /// </summary>
        /// <param name="jsonMessage">전송할 Json 메시지</param>
        /// <returns>메시지가 정상적으로 전송되었을 때, TRUE</returns>
        void SendSingleMessageToPlatform(string jsonMessage);
    }
}
