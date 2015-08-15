using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using LitJson;

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

