using System.Collections.Generic;
using BridgeApi.Media;

namespace AndroidApi.Media
{
    public class AndroidMediaDelegate : MediaDelegate
    {
        public List<BridgeApi.Media.ImageDirInfo> GetImageDirInfoList()
        {
            return ImageDirInfo.GetDirInfoList(AndroidUtils.GetActivityObject());
        }

        public List<BridgeApi.Media.VideoDirInfo> GetVideoDirInfoList()
        {
            return VideoDirInfo.GetDirInfoList(AndroidUtils.GetActivityObject());
        }
    }
}
