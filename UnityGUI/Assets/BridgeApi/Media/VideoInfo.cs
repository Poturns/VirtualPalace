namespace BridgeApi.Media
{
    public interface VideoInfo : BaseInfo
    {
        /// <summary>
        /// 크기
        /// </summary>
        long Size { get; set; }

        /// <summary>
        /// 재생시간
        /// </summary>
        long Duration { get; set; }

        /// <summary>
        /// 비디오의 처음 프레임의 이미지를 생성한다.
        /// </summary>
        /// <param name="fileName">비디오의 처음 프레임의 이미지가 저장되는 파일의 이름</param>
        /// <returns>비디오의 처음 프레임의 이미지가 저장된 파일 경로</returns>
        string GetFirstFrameThumbnailPath(string fileName);

        /// <summary>
        /// 비디오의 처음 프레임의 이미지를 생성한다.
        /// </summary>
        /// <returns>비디오의 처음 프레임의 이미지가 저장된 파일 경로</returns>
        string GetFirstFrameThumbnailPath();

    }

}

