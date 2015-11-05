using BridgeApi.Controller;
using UnityEngine;

namespace MyScript.objects
{
    public class VRObject : IDatabaseObject
    {
        /// <summary>
        /// VR_Table에서의 id
        /// </summary>
        public int ID { get; protected set; }


        public string Name { get; protected set; }
        public string ParentName { get; protected set; }

        public int ModelType { get; protected set; }

        public float PosX { get; protected set; }
        public float PosY { get; protected set; }
        public float PosZ { get; protected set; }

        public float RotateX { get; protected set; }
        public float RotateY { get; protected set; }
        public float RotateZ { get; protected set; }
        public float RotateW { get; protected set; }

        public float SizeX { get; protected set; }
        public float SizeY { get; protected set; }
        public float SizeZ { get; protected set; }

        public int ResID { get; protected set; }
        public string ResContents { get; protected set; }
        public string ResTitle { get; protected set; }

        public KIND_SOURCE SourceKind { get { return (KIND_SOURCE)ResID; } }
        public OBJ_LIST ObjKind { get { return (OBJ_LIST)ModelType; } }

        public Vector3 Position { get { return new Vector3(PosX, PosY, PosZ); } }
        public Quaternion Rotation { get { return new Quaternion(RotateX, RotateY, RotateZ, RotateW); } }
        public Vector3 Scale { get { return new Vector3(SizeX, SizeY, SizeZ); } }

        private VRObject()
        {
        }

        public class Builder
        {
            private VRObject vrObject;

            public Builder(string name, KIND_SOURCE sourceKind, OBJ_LIST objectKind)
            {
                vrObject = new VRObject();
                vrObject.Name = name;
                vrObject.ModelType = (int)objectKind;

                vrObject.ResID = (int)sourceKind;
            }

            public Builder SetParentName(string name)
            {
                vrObject.ParentName = name;
                return this;
            }

            public Builder SetPosX(float f)
            {
                vrObject.PosX = f;
                return this;
            }
            public Builder SetPosY(float f)
            {
                vrObject.PosY = f;
                return this;
            }

            public Builder SetPosZ(float f)
            {
                vrObject.PosZ = f;
                return this;
            }

            public Builder SetPosition(Vector3 vector)
            {
                return SetPosX(vector.x)
                    .SetPosY(vector.y)
                    .SetPosZ(vector.z);
            }

            public Builder SetRotateX(float f)
            {
                vrObject.RotateX = f;
                return this;
            }
            public Builder SetRotateY(float f)
            {
                vrObject.RotateY = f;
                return this;
            }
            public Builder SetRotateZ(float f)
            {
                vrObject.RotateZ = f;
                return this;
            }
            public Builder SetRotateW(float f)
            {
                vrObject.RotateW = f;
                return this;
            }


            public Builder SetPosition(Quaternion rotate)
            {
                return SetRotateX(rotate.x)
                    .SetRotateY(rotate.y)
                    .SetRotateZ(rotate.z)
                    .SetRotateW(rotate.w);
            }

            public Builder SetSizeX(float f)
            {
                vrObject.SizeX = f;
                return this;
            }
            public Builder SetSizeY(float f)
            {
                vrObject.SizeX = f;
                return this;
            }
            public Builder SetSizeZ(float f)
            {
                vrObject.SizeX = f;
                return this;
            }

            public Builder SetScale(Vector3 vector)
            {
                return SetSizeX(vector.x)
                    .SetSizeY(vector.y)
                    .SetSizeZ(vector.z);
            }


            public Builder SetResTitle(string s)
            {
                vrObject.ResTitle = s;
                return this;
            }

            public Builder SetResContents(string s)
            {
                vrObject.ResContents = s;
                return this;
            }

            public VRObject Build()
            {
                return vrObject;
            }
        }

    }
}
