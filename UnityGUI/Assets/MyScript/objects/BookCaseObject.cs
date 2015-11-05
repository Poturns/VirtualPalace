using BridgeApi.Controller;

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

    }
}
