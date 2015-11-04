using UnityEngine;
using System.IO;
using System.Text;

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

    public sealed class Text
    {
        private Text() { }

        /// <summary>
        /// TextMesh에 주어진 string을 입힌다.
        /// </summary>
        /// <param name="tm">문자열이 기록될 TextMesh</param>
        /// <param name="T">TextMesh에 기록할 문자열</param>
        public static void InputTextMesh(TextMesh tm, string T)
        {
            int StrSize = T.Length;
            StringBuilder sb = new StringBuilder();
            int i = 0;
            while (i < StrSize)
            {
                if (i % 17 == 0 && i > 0) sb.Append('\n');
                sb.Append(T[i]);
                i++;
            }
            sb.Append('\n');
            tm.text = sb.ToString();
        }
    }
}
