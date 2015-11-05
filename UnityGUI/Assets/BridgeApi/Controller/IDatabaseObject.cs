using System;
using System.Collections.Generic;

namespace BridgeApi.Controller
{
    public interface IDatabaseObject
    {
        /// <summary>
        /// Database에서의 pk, object를 구분하는 요소
        /// </summary>
        int ID { get; }

        /// <summary>
        /// K-V 배열로 필드를 반환한다. Database 접근 용도로 사용된다.
        /// </summary>
        /// <returns>K-V 배열</returns>
        KeyValuePair<Enum, string>[] ConvertToPairs();
        
    }
}
