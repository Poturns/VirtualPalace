using System.Collections.Generic;
using UnityEngine;
using LitJson;

namespace AndroidApi.Media
{
    internal class ImageInfo : BridgeApi.Media.ImageInfo
    {
        public string DisplayName { get; set; }

        public string DirName { get; set; }

        public int ID { get; set; }

        public string Path { get; set; }

        internal ImageInfo()
        {
        }

        internal static ImageInfo parseJSON(JsonData jData)
        {
            //Debug.Log (jData.ToString ());
            ImageInfo info = new ImageInfo()
            {
                ID = (int)jData["id"],
                Path = (string)jData["path"],
                DisplayName = (string)jData["displayName"],
                DirName = (string)jData["dirName"]
            };

            return info;

        }

        internal static List<BridgeApi.Media.ImageInfo> GetImageInfoList(AndroidJavaObject activity, string dirName)
        {
            using (AndroidJavaClass imageInfoClass = new AndroidJavaClass(AndroidMediaConstants.ImageInfoClassName))
            {
                string listJson = imageInfoClass.CallStatic<string>(AndroidMediaConstants.GetJsonInfoListMethodName, activity, dirName);

                JsonData jData = JsonMapper.ToObject(listJson);
                int count = jData.Count;

                List<BridgeApi.Media.ImageInfo> list = new List<BridgeApi.Media.ImageInfo>(count);
                for (int i = 0; i < count; i++)
                {
                    ImageInfo info = parseJSON(jData[i].ToJson());

                    list.Add(info);

                }

                return list;
            }
        }

    }
}

