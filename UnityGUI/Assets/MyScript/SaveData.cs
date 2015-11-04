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
public class SaveData : ISerializable {


	protected float Posx;
	protected float Posy;
	protected float Posz;

	protected float Rotx;
	protected float Roty;
	protected float Rotz;
	protected float Rotw;

	protected float Scalex;
	protected float Scaley;
	protected float Scalez;
	
	protected int Sourcekind;
	protected int ModelKind;

	protected string Contents;

	public KIND_SOURCE Source
	{
		get
		{
			return (KIND_SOURCE)Sourcekind;
		}
	}
	public OBJ_LIST ObjKind
	{
		get
		{
			return (OBJ_LIST)ModelKind;
		}
	}
	public string ObjName;
	protected string ParentName;

	public Vector3 Pos
	{
		get{ return new Vector3 (Posx, Posy, Posz);}
	}
	public Quaternion Rot
	{
		get{return new Quaternion(Rotx, Roty, Rotz, Rotw);}
	}
	public Vector3 Scale
	{
		get{return new Vector3(Scalex , Scaley , Scalez);}
	}
	public  void InitData(string Name , Vector3 _Pos , Quaternion _Rot , Vector3 _Scale ,
	                     int _sourcce , int _model  , string _Parent , string ContentsText)
	{
		Posx = _Pos.x;
		Posy = _Pos.y;
		Posz = _Pos.z;

		Rotx = _Rot.x;
		Roty = _Rot.y;
		Rotz = _Rot.z;
		Rotw = _Rot.w;

		Scalex = _Scale.x;
    	Scaley = _Scale.y;
    	Scalez = _Scale.z;

		Sourcekind = _sourcce;
		ModelKind = _model;
		ObjName = Name;
		ParentName = _Parent;
		Contents = ContentsText;
	}
	public SaveData(){}
	public SaveData(SerializationInfo info, StreamingContext ctx)
	{

		Posx = (float)info.GetValue ("Posx", typeof(float));
		Posy = (float)info.GetValue ("Posy", typeof(float));
		Posz = (float)info.GetValue ("Posz", typeof(float));

		Rotx = (float)info.GetValue ("Rotx", typeof(float));
		Roty = (float)info.GetValue ("Roty", typeof(float));
		Rotz = (float)info.GetValue ("Rotz", typeof(float));
		Rotw = (float)info.GetValue ("Rotw", typeof(float));

		Scalex = (float)info.GetValue ("Scalex", typeof(float));
		Scaley = (float)info.GetValue ("Scaley", typeof(float));
		Scalez = (float)info.GetValue ("Scalez", typeof(float));

		Sourcekind = (int)info.GetValue ("Sourcekind", typeof(int));
		ModelKind = (int)info.GetValue ("ModelKind", typeof(int));

		ObjName = (string)info.GetValue ("ObjName", typeof(string));
		ParentName= (string)info.GetValue ("ParentName", typeof(string));
		Contents= (string)info.GetValue ("Contents", typeof(string));
	}
	public virtual void GetObjectData (SerializationInfo info , StreamingContext ctx)
	{
		info.AddValue ("Posx", (Posx));
		info.AddValue ("Posy", (Posy));
		info.AddValue ("Posz", (Posz));

		info.AddValue ("Rotx", Rotx);
		info.AddValue ("Roty", Roty);
		info.AddValue ("Rotz", Rotz);
		info.AddValue ("Rotw", Rotw);

		info.AddValue ("Scalex",Scalex);
		info.AddValue ("Scaley",Scaley);
		info.AddValue ("Scalez",Scalez);
		
		info.AddValue ("Sourcekind", Sourcekind);
		info.AddValue ("ModelKind", ModelKind);
		
		info.AddValue ("ObjName", ObjName);
		info.AddValue ("ParentName", ParentName);
		info.AddValue ("Contents", Contents);
	}

    public KeyValuePair<Enum, string>[] ConvertVRMetadataToPairs()
    {
        KeyValuePair<Enum, string>[] pairs = new KeyValuePair<Enum, string>[15];
        int i = 0;

        //GetType().GetMembers()
        pairs[i++] = new KeyValuePair<Enum, string>(VIRTUAL_FIELD.RES_ID, Sourcekind.ToString());
        pairs[i++] = new KeyValuePair<Enum, string>(VIRTUAL_FIELD.NAME, ObjName);
        pairs[i++] = new KeyValuePair<Enum, string>(VIRTUAL_FIELD.TYPE, ObjKind.ToString());

        pairs[i++] = new KeyValuePair<Enum, string>(VIRTUAL_FIELD.POS_X, Posx.ToString());
        pairs[i++] = new KeyValuePair<Enum, string>(VIRTUAL_FIELD.POS_Y, Posy.ToString());
        pairs[i++] = new KeyValuePair<Enum, string>(VIRTUAL_FIELD.POS_Z, Posz.ToString());

        pairs[i++] = new KeyValuePair<Enum, string>(VIRTUAL_FIELD.ROTATE_X, Rotx.ToString());
        pairs[i++] = new KeyValuePair<Enum, string>(VIRTUAL_FIELD.ROTATE_Y, Roty.ToString());
        pairs[i++] = new KeyValuePair<Enum, string>(VIRTUAL_FIELD.ROTATE_Z, Rotx.ToString());

        pairs[i++] = new KeyValuePair<Enum, string>(VIRTUAL_FIELD.SIZE_X, Scalex.ToString());
        pairs[i++] = new KeyValuePair<Enum, string>(VIRTUAL_FIELD.SIZE_Y, Scaley.ToString());
        pairs[i++] = new KeyValuePair<Enum, string>(VIRTUAL_FIELD.SIZE_Z, Scalez.ToString());

        pairs[i++] = new KeyValuePair<Enum, string>(VIRTUAL_FIELD.CONTAINER, "");
        pairs[i++] = new KeyValuePair<Enum, string>(VIRTUAL_FIELD.CONT_ORDER, "");
        pairs[i++] = new KeyValuePair<Enum, string>(VIRTUAL_FIELD.STYLE, Contents);

        return pairs;
    }
}
