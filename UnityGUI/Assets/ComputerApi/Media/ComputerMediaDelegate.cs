using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using BridgeApi.Media;
using System.IO;

namespace ComputerApi.Media
{
    public class ComputerMediaDelegate : MediaDelegate
    {
        public List<BridgeApi.Media.ImageDirInfo> GetImageDirInfoList()
        {
            return ImageDirInfo.GetImageDirInfoList();
        }

        public List<BridgeApi.Media.VideoDirInfo> GetVideoDirInfoList()
        {
            throw new NotImplementedException();
        }

        private const string IMAGE_ROOT = @"Assets/StreamingAssets/image/";
        private const string VIDEO_ROOT = @"Assets/StreamingAssets/video/";

        private class ImageInfo : BridgeApi.Media.ImageInfo
        {
            public static ImageInfo FromFileInfo(FileInfo fileInfo)
            {
                return new ImageInfo()
                {
                    ID = fileInfo.GetHashCode(),
                    Path = fileInfo.FullName,
                    DisplayName = fileInfo.Name,
                    DirName = fileInfo.DirectoryName
                };
            }

            public static List<BridgeApi.Media.ImageInfo> GetInfoList(DirectoryInfo dirInfo)
            {
                FileInfo[] childsInfo = dirInfo.GetFiles();

                List<BridgeApi.Media.ImageInfo> list = new List<BridgeApi.Media.ImageInfo>(childsInfo.Length);
                foreach (FileInfo info in childsInfo)
                {
                    list.Add(FromFileInfo(info));
                }

                return list;
            }

        }

        private class ImageDirInfo : BridgeApi.Media.ImageDirInfo
        {
            public static List<BridgeApi.Media.ImageDirInfo> GetImageDirInfoList()
            {
                DirectoryInfo imageDirRootInfo = new DirectoryInfo(IMAGE_ROOT);

                DirectoryInfo[] rootChilds = imageDirRootInfo.GetDirectories();
                int count = rootChilds.Length;

                List<BridgeApi.Media.ImageDirInfo> list = new List<BridgeApi.Media.ImageDirInfo>(count);

                foreach (DirectoryInfo info in rootChilds)
                {
                    ImageDirInfo dirInfo = new ImageDirInfo()
                    {
                        DirName = info.FullName,
                        FirstInfo = ImageInfo.FromFileInfo(info.GetFiles()[0])
                    };

                    list.Add(dirInfo);
                }

                return list;
            }

            public override List<BridgeApi.Media.ImageInfo> GetInfoList()
            {
                return ImageInfo.GetInfoList(new DirectoryInfo(DirName));
            }
        }

    }

}
