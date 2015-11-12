using System.Collections.Generic;

namespace BridgeApi.Media
{
    public interface BaseDirInfo<T> where T : BaseInfo
    {
        /// <summary>
        /// 디렉토리 이름
        /// </summary>
        string DirName { get; set; }

        /// <summary>
        /// 디렉토리에 존재하는 Info중 첫번째 Info
        /// </summary>
        T FirstInfo { get; set; }

        /// <summary>
        /// 디렉토리에 존재하는 모든 Info의 리스트를 반환한다.
        /// </summary>
        /// <returns>디렉토리에 존재하는 모든 Info의 리스트</returns>
        List<T> GetInfoListInDirectory();

    }
}

