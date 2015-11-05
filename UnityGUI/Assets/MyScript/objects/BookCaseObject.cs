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
            KeyValuePair<Enum, string>[] pairs = new KeyValuePair<Enum, string>[3];

            pairs[0] = new KeyValuePair<Enum, string>(VR_CONTAINER_FIELD.NAME, Name);
            pairs[1] = new KeyValuePair<Enum, string>(VR_CONTAINER_FIELD.Z_OFFSET, Z_Offset.ToString());
            pairs[2] = new KeyValuePair<Enum, string>(VR_CONTAINER_FIELD.COUNT, Count.ToString());

            return pairs;
        }
        
        public static BookCaseObject FromJSON(LitJson.JsonData jsonData)
        {
            Debug.Log("===== " + jsonData.ToJson());
            int id = int.Parse((string)jsonData[VR_CONTAINER_FIELD._ID.ToString()]);
            string name = (string)jsonData[VR_CONTAINER_FIELD.NAME.ToString()];
            float offset = JsonInterpreter.ParseFloatData(jsonData, VR_CONTAINER_FIELD.Z_OFFSET.ToString());
            int count = int.Parse((string)jsonData[VR_CONTAINER_FIELD.COUNT.ToString()]);

            return new BookCaseObject(id, name, offset,count);
        }
    
    }
}
