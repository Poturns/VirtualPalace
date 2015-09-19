using UnityEngine;
using System.IO;

namespace Utils
{
    /// <summary>
    /// �̹����� ���õ� ��ƿ��Ƽ Ŭ����
    /// </summary>
	public sealed class Image
	{
		private Image ()
		{
		}

        /// <summary>
        /// �ش� ����� �̹��� ������ �ҷ��´�.
        /// </summary>
        /// <param name="filePath">�̹��� ������ ���</param>
        /// <returns>�ش� ��ο� �����ϴ� �̹��� ����</returns>
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
