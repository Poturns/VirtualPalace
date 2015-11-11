using BridgeApi.Controller;
using BridgeApi.Controller.Request.Database;
using System;
using System.Collections.Generic;
using UnityEngine;

namespace MyScript.Objects
{
    public struct BookCaseObject : IDatabaseObject
    {
#if UNITY_EDITOR
        [SQLite4Unity3d.PrimaryKey, SQLite4Unity3d.AutoIncrement]
#endif
        public int ID { get; set; }
        public string Name { get; set; }
        public float Z_Offset { get; set; }
        public int Count { get; set; }

        public BookCaseObject(string name, float zOffset, int count, int id = -1)
        {
            ID = id;
            Name = name;
            Z_Offset = zOffset;
            Count = count;
        }
        /*
        public BookCaseObject(string name, float zOffset, int count) : this(name, zOffset, count, -1)
        {
        }
        */

        public KeyValuePair<Enum, string>[] ConvertToPairs()
        {
            KeyValuePair<Enum, string>[] pairs = new KeyValuePair<Enum, string>[4];

            pairs[0] = new KeyValuePair<Enum, string>(VR_CONTAINER_FIELD._ID, ID.ToString());
            pairs[1] = new KeyValuePair<Enum, string>(VR_CONTAINER_FIELD.NAME, Name);
            pairs[2] = new KeyValuePair<Enum, string>(VR_CONTAINER_FIELD.Z_OFFSET, Z_Offset.ToString());
            pairs[3] = new KeyValuePair<Enum, string>(VR_CONTAINER_FIELD.COUNT, Count.ToString());

            return pairs;
        }

        public static BookCaseObject FromJSON(LitJson.JsonData jsonData)
        {
            //Debug.Log("===== " + jsonData.ToJson());
            int id = JsonInterpreter.ParseIntData(jsonData, VR_CONTAINER_FIELD._ID.ToString());

            string name = JsonInterpreter.ParseStringData(jsonData, VR_CONTAINER_FIELD.NAME.ToString());
            if (name[0] == '\'')
                name = name.Remove(0);
            if (name[name.Length - 1] == '\'')
                name = name.Remove(name.Length - 1);

            float offset = JsonInterpreter.ParseFloatData(jsonData, VR_CONTAINER_FIELD.Z_OFFSET.ToString());
            int count = JsonInterpreter.ParseIntData(jsonData, VR_CONTAINER_FIELD.COUNT.ToString());

            return new BookCaseObject(name, offset, count, id);
        }

        public override string ToString()
        {
            return this.StringForm();
        }

        public string ToJSON()
        {
            return this.JsonForm();
        }

        public bool IsInvalid()
        {
            return ID < 0;
        }
    }
}
