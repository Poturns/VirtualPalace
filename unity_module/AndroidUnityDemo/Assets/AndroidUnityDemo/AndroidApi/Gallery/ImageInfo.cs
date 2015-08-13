using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using LitJson;

namespace AndroidApi.Gallery
{
	public class ImageInfo
	{

		public string DisplayName {
			get;
			set;
		}

		public string DirName {
			get;
			set;
		}

		public int ID {
			get;
			set;
		}

		public string ImagePath {
			get;
			set;
		}

		ImageInfo ()
		{
		}

		internal static ImageInfo parseJson (JsonData jData)
		{
			//Debug.Log (jData.ToString ());
			ImageInfo info = new ImageInfo (){
				ID =(int)jData ["id"],
				ImagePath = (string)jData["imagePath"],
				DisplayName = (string)jData["displayName"],
				DirName = (string)jData["bucketName"]
			};

			return info;

		}
		
		public List<ImageInfo> GetImageInfoList (AndroidJavaObject activity)
		{
			using (AndroidJavaClass imageInfoClass = new AndroidJavaClass("kr.poturns.util.image.ImageInfo")) {
				string listJson = imageInfoClass.CallStatic<string> ("getJsonImageInfoList", activity, DirName);

				JsonData jData = JsonMapper.ToObject (listJson);
				int count = jData.Count;

				List<ImageInfo> list = new List<ImageInfo> (count);
				for (int i = 0; i < count; i++) {
					ImageInfo info = parseJson (jData [i].ToJson ());
					
					list.Add (info);
					
				}
				
				return list;
			}
		}

	}
}

