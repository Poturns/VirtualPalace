using BridgeApi.Controller;
using BridgeApi.Controller.Request.Database;
using System;
using System.Collections.Generic;
using UnityEngine;

namespace MyScript.objects
{
    public class BookCaseObject : IDatabaseObject
    {
        public int ID { get; protected set; }
        public string Name { get; protected set; }
        public float Z_Offset { get; protected set; }
        public int Count { get; protected set; }

        public BookCaseObject(int id, string name, float zOffset, int count)
        {
            ID = id;
            Name = name;
            Z_Offset = zOffset;
            Count = count;
        }

        public BookCaseObject(string name, float zOffset, int count) : this(-1, name, zOffset, count)
        {
        }


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
            string name = (string)jsonData[VR_CONTAINER_FIELD.NAME.ToString()];
            if (name[0] == '\'')
                name = name.Remove(0);
            if (name[name.Length - 1] == '\'')
                name = name.Remove(name.Length - 1);

            float offset = JsonInterpreter.ParseFloatData(jsonData, VR_CONTAINER_FIELD.Z_OFFSET.ToString());
            int count = JsonInterpreter.ParseIntData(jsonData, VR_CONTAINER_FIELD.COUNT.ToString());

            return new BookCaseObject(id, name, offset, count);
        }

        public override string ToString()
        {
            return "BookCaseObject : [ id : " + ID + ", name : " + Name + ", zOffset : " + Z_Offset + ", count : " + Count + " ]";
        }

    }
}
