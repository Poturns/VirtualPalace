using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using LitJson;

namespace AndroidApi.Gallery
{
	public class ImageDirInfo
	{
		public string DirName {
			get;
			set;
		}
	
		public ImageInfo FirstImageInfo {
			get;
			set;
		}

		private ImageDirInfo ()
		{
		}

		public static List<ImageDirInfo> GetImageDirInfoList (AndroidJavaObject activity)
		{
			using (AndroidJavaClass imageBucketInfoClass = new AndroidJavaClass("kr.poturns.util.image.ImageBucketInfo")) {
				string listJson = imageBucketInfoClass.CallStatic<string> ("getJsonImageBucketList", activity);

				//Debug.Log(listJson);


				JsonData jData = JsonMapper.ToObject (listJson);

				int count = jData.Count;

				List<ImageDirInfo> list = new List<ImageDirInfo>(count);
				for (int i = 0 ; i < count ; i++)
				{
					ImageDirInfo info = new ImageDirInfo (){
						DirName = (string)jData [i]["bucketName"],
						FirstImageInfo = ImageInfo.parseJson (jData [i]["firstImageInfo"])
					};

					list.Add(info);

				}

				return list;
			}
		}

	}
}

