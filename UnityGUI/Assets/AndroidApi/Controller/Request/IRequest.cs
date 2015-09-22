namespace AndroidApi.Controller.Request
{
    /// <summary>
    /// Android으로의 요청
    /// </summary>
    public interface IRequest
    {
        /// <summary>
        /// 작성한 요청을 Json Message로 변환한다.
        /// </summary>
        /// <returns>요청이 기술된 Json Message</returns>
        string ToJson();

    }

}
