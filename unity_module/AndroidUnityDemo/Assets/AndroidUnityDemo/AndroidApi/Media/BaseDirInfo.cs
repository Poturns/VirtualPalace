namespace AndroidApi.Media
{
    public abstract class BaseDirInfo <T> where T : BaseInfo
	{
		public string DirName {
			get;
			protected set;
		}
		
		public T FirstInfo {
			get;
			protected set;
		}

	}
}

