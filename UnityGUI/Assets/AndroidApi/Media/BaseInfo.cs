namespace AndroidApi.Media
{
	public abstract class BaseInfo
	{
		protected const string GetJsonInfoListMethodName = "getJSONInfoList";

		public string DisplayName {
			get;
			protected set;
		}
		
		public string DirName {
			get;
			protected set;
		}
		
		public int ID {
			get;
			protected set;
		}
		
		public string Path {
			get;
			protected set;
		}

	}
}

