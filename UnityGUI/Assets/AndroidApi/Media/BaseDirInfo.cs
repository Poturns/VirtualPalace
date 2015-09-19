using UnityEngine;
using System.Collections.Generic;

namespace AndroidApi.Media
{
    public abstract class BaseDirInfo<T> where T : BaseInfo
    {
        protected const string GetJsonDirListMethodName = "getJSONDirList";

        public string DirName
        {
            get;
            protected set;
        }

        public T FirstInfo
        {
            get;
            protected set;
        }

        public abstract List<T> GetInfoList(AndroidJavaObject androidJavaObject);

    }
}

