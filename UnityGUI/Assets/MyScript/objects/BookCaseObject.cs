using BridgeApi.Controller;

namespace MyScript.objects
{
    public class BookCaseObject : IDatabaseObject
    {
        public int ID { get; set; }
        public string Name { get; set; }
        public float Z_Offset { get; set; }
        public int Count { get; set; }
        
        public BookCaseObject(int id, string name, float zOffset, int count)
        {
            ID = id;
            Name = name;
            Z_Offset = zOffset;
            Count = count;
        }

    }
}
