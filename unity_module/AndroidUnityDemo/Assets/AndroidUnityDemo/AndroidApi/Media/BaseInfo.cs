using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using LitJson;

namespace AndroidApi.Media
{
	public abstract class BaseInfo
	{
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

