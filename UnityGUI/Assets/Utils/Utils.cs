using UnityEngine;
using System.IO;

namespace Utils
{
    /// <summary>
    /// 이미지와 관련된 유틸리티 클래스
    /// </summary>
	public sealed class Image
	{
		private Image ()
		{
		}

        /// <summary>
        /// 해당 경로의 이미지 파일을 불러온다.
        /// </summary>
        /// <param name="filePath">이미지 파일의 경로</param>
        /// <returns>해당 경로에 존재하는 이미지 파일</returns>
		public static Texture2D Load (string filePath)
		{
			Texture2D tex = null;
			byte[] fileData;
			
			if (File.Exists (filePath)) {
				fileData = File.ReadAllBytes (filePath);
				tex = new Texture2D (2, 2);
				tex.LoadImage (fileData);
			}
			return tex;
		}
		
	}
}
