using LitJson;
using System;
using System.Collections.Generic;
using UnityEngine;

namespace AndroidApi.Media
{
    internal class VideoInfo : BridgeApi.Media.VideoInfo
    {
        internal VideoInfo()
        {
        }

        private static long parseLongJSONData(JsonData jData, string key)
        {
            long l;
            try
            {
                l = (long)jData[key];
            }
            catch (InvalidCastException)
            {
                //Debug.LogException(e);
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

        internal static List<BridgeApi.Media.VideoInfo> GetInfoList(AndroidJavaObject activity, string dirName)
        {
            using (AndroidJavaClass videoInfoClass = new AndroidJavaClass(AndroidMediaConstants.VideoInfoClassName))
            {
                string listJson = videoInfoClass.CallStatic<string>(AndroidMediaConstants.GetJsonInfoListMethodName, activity, dirName);

                JsonData jData = JsonMapper.ToObject(listJson);
                int count = jData.Count;

                List<BridgeApi.Media.VideoInfo> list = new List<BridgeApi.Media.VideoInfo>(count);
                for (int i = 0; i < count; i++)
                {
                    VideoInfo info = parseJSON(jData[i].ToJson());

                    list.Add(info);

                }

                return list;
            }
        }

        /// <summary>
        /// 비디오의 처음 프레임의 이미지를 생성한다.
        /// </summary>
        /// <param name="fileName">비디오의 처음 프레임의 이미지가 저장되는 파일의 이름</param>
        /// <returns>비디오의 처음 프레임의 이미지가 저장된 파일 경로</returns>
        public override string GetFirstFrameThumbnailPath(string fileName)
        {
            using (AndroidJavaClass videoInfoClass = new AndroidJavaClass(AndroidMediaConstants.VideoInfoClassName))
            {
                return videoInfoClass.CallStatic<string>("getFirstFrameThumbnail", AndroidUtils.GetActivityObject(), fileName, Path);
            }
        }

    }

}

