namespace BridgeApi.Controller
{
    public interface IDatabaseObject
    {
        /// <summary>
        /// Database에서의 pk, object를 구분하는 요소
        /// </summary>
        int ID { get; set; }

        // 인서트할때 k-v 배열
    }
}
