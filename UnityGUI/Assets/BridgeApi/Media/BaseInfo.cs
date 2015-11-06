namespace BridgeApi.Media
{
    public abstract class BaseInfo
    {
        public string DisplayName
        {
            get;
            protected internal set;
        }

        public string DirName
        {
            get;
            protected internal set;
        }

        public int ID
        {
            get;
            protected internal set;
        }

        public string Path
        {
            get;
            protected internal set;
        }

    }
}

