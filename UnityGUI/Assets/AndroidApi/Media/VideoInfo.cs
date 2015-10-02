using LitJson;
using System;
using System.Collections.Generic;
using UnityEngine;

namespace AndroidApi.Media
{
    public class VideoInfo : BaseInfo
    {
        protected const string VideoInfoClassName = "kr.poturns.virtualpalace.media.video.VideoInfo";

        internal VideoInfo()
        {
        }

        public long Size
        {
            get;
            protected set;
        }

        public long Duration
        {
            get;
            protected set;
        }

        private static long parseLongJSONData(JsonData jData, string key)
        {
            long l;
            try
            {
                l = (long)jData[key];
            }
            catch (InvalidCastException e)
            {
                l = (int)jData[key];
            }

            return l;
        }

        internal static VideoInfo parseJSON(JsonData jData)
        {
            VideoInfo info = new VideoInfo()
            {
                ID = int.Parse(jData["id"].ToString()),
                Path = (string)jData["path"],
                DisplayName = (string)jData["displayName"],
                DirName = (string)jData["dirName"],
                Size = parseLongJSONData(jData, "size"),
                Duration = parseLongJSONData(jData, "duration"),
            };

            return info;
        }

        internal static List<VideoInfo> GetInfoList(AndroidJavaObject activity, string dirName)
        {
            using (AndroidJavaClass imageInfoClass = new AndroidJavaClass(VideoInfoClassName))
            {
                string listJson = imageInfoClass.CallStatic<string>(GetJsonInfoListMethodName, activity, dirName);

                JsonData jData = JsonMapper.ToObject(listJson);
                int count = jData.Count;

                List<VideoInfo> list = new List<VideoInfo>(count);
                for (int i = 0; i < count; i++)
                {
                    VideoInfo info = parseJSON(jData[i].ToJson());

                    list.Add(info);

                }

                return list;
            }
        }

        public string GetFirstFrameThumbnailPath(string fileName)
        {
            using (AndroidJavaClass imageInfoClass = new AndroidJavaClass(VideoInfoClassName))
            {
                return imageInfoClass.CallStatic<string>("getFirstFrameThumbnail", fileName, Path);
            }
        }
    }
}

