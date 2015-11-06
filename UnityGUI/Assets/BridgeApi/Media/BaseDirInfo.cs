using System.Collections.Generic;

namespace BridgeApi.Media
{
    public abstract class BaseDirInfo<T> where T : BaseInfo
    {
        public string DirName
        {
            get;
            protected internal set;
        }

        public T FirstInfo
        {
            get;
            protected internal set;
        }

        public abstract List<T> GetInfoList();

    }
}

