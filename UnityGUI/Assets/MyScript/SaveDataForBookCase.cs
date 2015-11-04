using UnityEngine; 

using System.Text;
using System.IO;
using System.Runtime.Serialization.Formatters.Binary;

using System;
using System.Runtime.Serialization;
using System.Reflection;

[Serializable ()]
public class SaveDataForBookCase : SaveData 
{
	public float CurZOffest;
	public int	Cnt;

	public void InitData(string Name , Vector3 _Pos , Quaternion _Rot , Vector3 _Scale ,
	                     int _sourcce , int _model  , string _Parent ,string ContentsText, float ZOff , int cnt)
	{
		base.InitData (Name ,_Pos , _Rot , _Scale , _sourcce , _model , _Parent,ContentsText);
		CurZOffest = ZOff;
		Cnt = cnt;
	}
	public SaveDataForBookCase() :base()
	{
	}

	public SaveDataForBookCase(SerializationInfo info , StreamingContext ctx) :base(info,ctx)
	{
		CurZOffest = (float)info.GetValue ("CurZOffest", typeof(float));
		Cnt= (int)info.GetValue ("Cnt", typeof(int));
	}
	public override void GetObjectData (SerializationInfo info , StreamingContext ctx)
	{
		base.GetObjectData (info, ctx);
		info.AddValue ("CurZOffest", CurZOffest);
		info.AddValue ("Cnt", Cnt);
	}
}
