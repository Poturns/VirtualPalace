using UnityEngine;

using System.Text;
using System.IO;
using System.Runtime.Serialization.Formatters.Binary;

using System;
using System.Runtime.Serialization;
using System.Reflection;
using System.Collections.Generic;
using BridgeApi.Controller.Request.Database;

[Serializable ()]
public class SaveDataForBookCase : SaveData 
{
	public float CurZOffest;
	public int	Cnt;

	public void InitData(string Name , Vector3 _Pos , Quaternion _Rot , Vector3 _Scale ,
	                     int _sourcce , int _model  , string _Parent ,string ContentsText, float ZOff , int cnt)
	{
		base.InitData (Name ,_Pos , _Rot , _Scale , _sourcce , _model , _Parent,ContentsText , "" );
		Key = 0;
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

    public new KeyValuePair<Enum, string>[] ConvertToPairs()
    {
        KeyValuePair<Enum, string>[] pairs = new KeyValuePair<Enum, string>[3];

        pairs[0] = new KeyValuePair<Enum, string>(VR_CONTAINER_FIELD.NAME, ObjName);
        pairs[1] = new KeyValuePair<Enum, string>(VR_CONTAINER_FIELD.Z_OFFSET, CurZOffest.ToString());
        pairs[2] = new KeyValuePair<Enum, string>(VR_CONTAINER_FIELD.COUNT, Cnt.ToString());

        return pairs;
    }

    public new static SaveDataForBookCase FromJson(LitJson.JsonData jsonData)
    {
        Debug.Log("===== " + jsonData.ToJson());
        SaveDataForBookCase data = new SaveDataForBookCase();

        data.Key = int.Parse((string)jsonData[VR_CONTAINER_FIELD._ID.ToString()]);
        data.ObjName = (string)jsonData[VR_CONTAINER_FIELD.NAME.ToString()];
        data.CurZOffest = GetFloatData(jsonData, VR_CONTAINER_FIELD.Z_OFFSET.ToString());
        data.Cnt = int.Parse((string)jsonData[VR_CONTAINER_FIELD.COUNT.ToString()]);

        return data;
    }
}
