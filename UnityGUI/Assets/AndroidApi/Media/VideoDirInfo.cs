using System.Collections.Generic;
using UnityEngine;
using LitJson;

namespace AndroidApi.Media
{
    internal class VideoDirInfo : BridgeApi.Media.VideoDirInfo
    {
       
        private VideoDirInfo()
        {
        }

        public static List<BridgeApi.Media.VideoDirInfo> GetDirInfoList(AndroidJavaObject activity)
        {
            using (AndroidJavaClass videoDirInfoClass = new AndroidJavaClass(AndroidMediaConstants.VideoDirInfoClassName))
            {
                string listJson = videoDirInfoClass.CallStatic<string>(AndroidMediaConstants.GetJsonDirListMethodName, activity);

                JsonData jData = JsonMapper.ToObject(listJson);

                int count = jData.Count;

                List<BridgeApi.Media.VideoDirInfo> list = new List<BridgeApi.Media.VideoDirInfo>(count);
                for (int i = 0; i < count; i++)
                {
                    VideoDirInfo info = new VideoDirInfo()
                    {
                        DirName = (string)jData[i]["dirName"],
                        FirstInfo = VideoInfo.parseJSON(jData[i]["firstInfo"])
                    };

                    list.Add(info);

                }

                return list;
            }
        }

        public override List<BridgeApi.Media.VideoInfo> GetInfoList()
        {
            return VideoInfo.GetInfoList(AndroidUtils.GetActivityObject(), DirName);
        }
    }
}

