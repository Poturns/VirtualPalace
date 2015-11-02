using UnityEngine;
using System.IO;
using System.Text;

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

    public sealed class Text
    {
        private Text() { }

        /// <summary>
        /// TextMesh�� �־��� string�� ������.
        /// </summary>
        /// <param name="tm">���ڿ��� ��ϵ� TextMesh</param>
        /// <param name="T">TextMesh�� ����� ���ڿ�</param>
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
