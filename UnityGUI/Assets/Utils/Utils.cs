using UnityEngine;
using UnityEngine.UI;
using System;
using System.IO;

namespace Utils
{
	public sealed class Image
	{
		private Image ()
		{
		}

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
