namespace BridgeApi.Media
{
    public abstract class VideoInfo : BaseInfo
    {
        protected internal VideoInfo()
        {
        }

        public long Size
        {
            get;
            protected internal set;
        }

        public long Duration
        {
            get;
            protected internal set;
        }

        /// <summary>
        /// 비디오의 처음 프레임의 이미지를 생성한다.
        /// </summary>
        /// <param name="fileName">비디오의 처음 프레임의 이미지가 저장되는 파일의 이름</param>
        /// <returns>비디오의 처음 프레임의 이미지가 저장된 파일 경로</returns>
        public abstract string GetFirstFrameThumbnailPath(string fileName);

        /// <summary>
        /// 비디오의 처음 프레임의 이미지를 생성한다.
        /// </summary>
        /// <returns>비디오의 처음 프레임의 이미지가 저장된 파일 경로</returns>
        public string GetFirstFrameThumbnailPath()
        {
            return GetFirstFrameThumbnailPath(DirName + "_" + DisplayName);
        }

    }

}

