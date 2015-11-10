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
       

        private class ImageDirInfo : BridgeApi.Media.ImageDirInfo
        {
            public string DirName { get; set; }

            public BridgeApi.Media.ImageInfo FirstInfo { get; set; }

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

            public List<BridgeApi.Media.ImageInfo> GetInfoListInDirectory()
            {
                return ImageInfo.GetInfoList(new DirectoryInfo(DirName));
            }

        }

        private class ImageInfo : BridgeApi.Media.ImageInfo
        {
            public string DirName { get; set; }

            public string DisplayName { get; set; }

            public int ID { get; set; }

            public string Path { get; set; }

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

        private class VideoDirInfo : BridgeApi.Media.VideoDirInfo
        {
            public string DirName { get; set; }

            public BridgeApi.Media.VideoInfo FirstInfo { get; set; }

            public static List<BridgeApi.Media.VideoDirInfo> GetVideoDirInfoList()
            {
                DirectoryInfo imageDirRootInfo = new DirectoryInfo(VIDEO_ROOT);

                DirectoryInfo[] rootChilds = imageDirRootInfo.GetDirectories();
                int count = rootChilds.Length;

                List<BridgeApi.Media.VideoDirInfo> list = new List<BridgeApi.Media.VideoDirInfo>(count);

                foreach (DirectoryInfo info in rootChilds)
                {
                    VideoDirInfo dirInfo = new VideoDirInfo()
                    {
                        DirName = info.FullName,
                        FirstInfo = VideoInfo.FromFileInfo(info.GetFiles()[0])
                    };

                    list.Add(dirInfo);
                }

                return list;
            }

            public List<BridgeApi.Media.VideoInfo> GetInfoListInDirectory()
            {
                return VideoInfo.GetInfoList(new DirectoryInfo(DirName));
            }
        }

        private class VideoInfo : BridgeApi.Media.VideoInfo
        {
            public long Size { get; set; }

            public long Duration { get; set; }

            public string DisplayName { get; set; }

            public string DirName { get; set; }

            public int ID { get; set; }

            public string Path { get; set; }

            public static VideoInfo FromFileInfo(FileInfo fileInfo)
            {
                return new VideoInfo()
                {
                    ID = fileInfo.GetHashCode(),
                    Path = fileInfo.FullName,
                    DisplayName = fileInfo.Name,
                    DirName = fileInfo.DirectoryName,
                    Size = fileInfo.Length,
                    //Duration
                };
            }

            public static List<BridgeApi.Media.VideoInfo> GetInfoList(DirectoryInfo dirInfo)
            {
                FileInfo[] childsInfo = dirInfo.GetFiles();

                List<BridgeApi.Media.VideoInfo> list = new List<BridgeApi.Media.VideoInfo>(childsInfo.Length);
                foreach (FileInfo info in childsInfo)
                {
                    list.Add(FromFileInfo(info));
                }

                return list;
            }

            public string GetFirstFrameThumbnailPath()
            {
                throw new NotImplementedException();
            }

            public string GetFirstFrameThumbnailPath(string fileName)
            {
                return GetFirstFrameThumbnailPath(DirName + "_" + DisplayName);
            }
        }
    }

}
