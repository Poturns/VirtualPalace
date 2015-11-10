namespace BridgeApi.Media
{
    public interface BaseInfo
    {
        /// <summary>
        /// 이름
        /// </summary>
        string DisplayName { get; set; }

        /// <summary>
        /// 디렉토리 이름
        /// </summary>
        string DirName { get; set; }

        int ID { get; set; }

        /// <summary>
        /// 파일의 경로
        /// </summary>
        string Path { get; set; }

    }
}

