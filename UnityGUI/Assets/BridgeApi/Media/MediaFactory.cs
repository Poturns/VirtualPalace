using System.Collections.Generic;
using UnityEngine;

namespace BridgeApi.Media
{
    public sealed class MediaFactory : MediaDelegate
    {
        private static readonly MediaFactory instance = new MediaFactory();

        public static MediaFactory GetInstance()
        {
            return instance;
        }

        private readonly MediaDelegate mediaDelegate;

        private MediaFactory()
        {
            if (Application.platform == RuntimePlatform.Android)
            {
                mediaDelegate = new AndroidApi.Media.AndroidMediaDelegate();
            }
            else if (Application.platform == RuntimePlatform.WindowsEditor)
            {
                mediaDelegate = new ComputerApi.Media.ComputerMediaDelegate();
            }
            else
            {
                mediaDelegate = new DummyMediaDelegateImpl();
            }
        }

        public List<ImageDirInfo> GetImageDirInfoList()
        {
            return mediaDelegate.GetImageDirInfoList();
        }
        public List<VideoDirInfo> GetVideoDirInfoList()
        {
            return mediaDelegate.GetVideoDirInfoList();
        }

        private class DummyMediaDelegateImpl : MediaDelegate
        {
            public List<ImageDirInfo> GetImageDirInfoList()
            {
                Debug.LogWarning("=== calling dummy GetImageDirInfoList()");
                return new List<ImageDirInfo>();
            }

            public List<VideoDirInfo> GetVideoDirInfoList()
            {
                Debug.LogWarning("=== calling dummy GetVideoDirInfoList()");
                return new List<VideoDirInfo>();
            }
        }
    }

    public interface MediaDelegate
    {
        List<ImageDirInfo> GetImageDirInfoList();
        List<VideoDirInfo> GetVideoDirInfoList();
    }


}
