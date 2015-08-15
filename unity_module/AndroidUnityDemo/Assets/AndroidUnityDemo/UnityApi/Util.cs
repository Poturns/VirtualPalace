using UnityEngine;
using UnityEngine.UI;
using System;
using System.IO;

public class Util
{
	private Util ()
	{
	}

	public static class Image
	{
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