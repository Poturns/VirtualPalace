using System.Collections.Generic;
using UnityEngine;
using LitJson;

namespace AndroidApi.Media
{
	public class VideoDirInfo : BaseDirInfo<VideoInfo>
	{
		protected const string VideoDirInfoClassName = "kr.poturns.virtualpalace.media.video.VideoDirInfo";

		private VideoDirInfo ()
		{
		}
		
		public static List<VideoDirInfo> GetDirInfoList (AndroidJavaObject activity)
		{
			using (AndroidJavaClass videoDirInfoClass = new AndroidJavaClass(VideoDirInfoClassName)) {
				string listJson = videoDirInfoClass.CallStatic<string> (GetJsonDirListMethodName, activity);
				
				JsonData jData = JsonMapper.ToObject (listJson);
				
				int count = jData.Count;
				
				List<VideoDirInfo> list = new List<VideoDirInfo> (count);
				for (int i = 0; i < count; i++) {
					VideoDirInfo info = new VideoDirInfo (){
						DirName = (string)jData [i]["dirName"],
						FirstInfo = VideoInfo.parseJSON (jData [i]["firstInfo"])
					};
					
					list.Add (info);
					
				}
				
				return list;
			}
		}

		public override List<VideoInfo> GetInfoList (AndroidJavaObject activity)
		{
			return VideoInfo.GetInfoList (activity);
		}
	}
}

