using System.Collections.Generic;
using UnityEngine;
using LitJson;

namespace AndroidApi.Media
{
    internal class ImageDirInfo : BridgeApi.Media.ImageDirInfo
    {

        public string DirName { get; set; }

        public BridgeApi.Media.ImageInfo FirstInfo { get; set; }


        private ImageDirInfo()
        {
        }

        public static List<BridgeApi.Media.ImageDirInfo> GetDirInfoList(AndroidJavaObject activity)
        {
            using (AndroidJavaClass imageBucketInfoClass = new AndroidJavaClass(AndroidMediaConstants.ImageDirInfoClassName))
            {
                string listJson = imageBucketInfoClass.CallStatic<string>(AndroidMediaConstants.GetJsonDirListMethodName, activity);

                //Debug.Log(listJson);


                JsonData jData = JsonMapper.ToObject(listJson);

                int count = jData.Count;

                List<BridgeApi.Media.ImageDirInfo> list = new List<BridgeApi.Media.ImageDirInfo>(count);
                for (int i = 0; i < count; i++)
                {
                    ImageDirInfo info = new ImageDirInfo()
                    {
                        DirName = (string)jData[i]["dirName"],
                        FirstInfo = ImageInfo.parseJSON(jData[i]["firstInfo"])
                    };

                    list.Add(info);

                }

                return list;
            }
        }

        public List<BridgeApi.Media.ImageInfo> GetInfoListInDirectory()
        {
            return ImageInfo.GetImageInfoList(AndroidUtils.GetActivityObject(), DirName);
        }

    }
}

