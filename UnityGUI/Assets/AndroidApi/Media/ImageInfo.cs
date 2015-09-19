using System.Collections.Generic;
using UnityEngine;
using LitJson;

namespace AndroidApi.Media
{
    public class ImageInfo : BaseInfo
	{
		public const string ImageInfoClassName = "kr.poturns.virtualpalace.media.image.ImageInfo";
		internal ImageInfo ()
		{
		}

		internal static ImageInfo parseJSON (JsonData jData)
		{
			//Debug.Log (jData.ToString ());
			ImageInfo info = new ImageInfo (){
				ID =(int)jData ["id"],
				Path = (string)jData["path"],
				DisplayName = (string)jData["displayName"],
				DirName = (string)jData["dirName"]
			};

			return info;

		}
		
		public List<ImageInfo> GetImageInfoList (AndroidJavaObject activity)
		{
			using (AndroidJavaClass imageInfoClass = new AndroidJavaClass(ImageInfoClassName)) {
				string listJson = imageInfoClass.CallStatic<string> (GetJsonInfoListMethodName, activity, DirName);

				JsonData jData = JsonMapper.ToObject (listJson);
				int count = jData.Count;

				List<ImageInfo> list = new List<ImageInfo> (count);
				for (int i = 0; i < count; i++) {
					ImageInfo info = parseJSON (jData [i].ToJson ());
					
					list.Add (info);
					
				}
				
				return list;
			}
		}

	}
}

