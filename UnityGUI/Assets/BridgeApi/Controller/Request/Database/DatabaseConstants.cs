
namespace BridgeApi.Controller.Request.Database
{
    public sealed class DatabaseConstants
    {
        ///<summary>
        /// 현재 위치에서 일정 범위 내 존재하는 데이터를 찾는다.
        ///</summary>
        public const string QUERY_NEAR_ITEMS = "query_near_items";
        ///<summary>
        /// VR 아이템에 렌더링할 아이템 데이터를 찾는다.
        ///</summary>
        public const string QUERY_ALL_VR_ITEMS = "query_all_vr_items";

        /// <summary>
        /// BookCase 데이터를 모두 찾는다.
        /// </summary>
        public const string QUERY_ALL_VR_BOOKCASES = "query_all_vr_bookcases";

        public const string QUERY_SAVE_ALL_VR_ITEMS = "save_all_vr_items";

        //public const string QUERY_UPDATE_VR_ITEMS = "update_all_vr_items";

        public const string QUERY_UPDATE_VR_BOOKCASES = "update_all_vr_bookcases";

        ///<summary>
        /// (응답 반환시) 쿼리 결과 KEY
        ///<para/>
        /// Ex)<para/>
        /// {<para/>
        ///     "return" : [<para/>
        ///          { "_id" : 1, "res_id" : 1, .... },<para/>
        ///          { "_id" : 2, "res_id" : 2, .... },<para/>
        ///     ]<para/>
        /// }<para/>
        ///</summary>
        public const string QUERY_RESULT = "return";

        public const string TABLE_AR = "ar";
        public const string TABLE_VR = "vr";
        public const string TABLE_RES = "res";
        public const string TABLE_VR_CONTAINER = "bookcase";

        public const string SELECT = "select_";
        public const string INSERT = "insert_";
        public const string UPDATE = "update_";
        public const string DELETE = "delete_";

        public const string OPERATION_ALLOW_EQUAL = "allow_equal";

        public const string OPERATION_SET = "set";
        public const string OPERATION_WHERE = "where";
        public const string OPERATION_WHERE_NOT = "where_not";
        public const string OPERATION_WHERE_GREATER = "where_greater";
        public const string OPERATION_WHERE_SMALLER = "where_smaller";
        public const string OPERATION_WHERE_FROM = "where_from";
        public const string OPERATION_WHERE_TO = "where_to";
        public const string OPERATION_WHERE_LIKE = "where_like";

    }

    /// <summary>
    /// Database에서 조작할 Table
    /// </summary>
    public enum Table
    {
        VR, AR, RES, VR_CONTAINER
    }

    public static class Tables
    {
        public static string GetName(this Table table)
        {
            switch (table)
            {
                case Table.AR:
                    return DatabaseConstants.TABLE_AR;
                case Table.VR:
                    return DatabaseConstants.TABLE_VR;
                case Table.RES:
                    return DatabaseConstants.TABLE_RES;
                case Table.VR_CONTAINER:
                    return DatabaseConstants.TABLE_VR_CONTAINER;
                default:
                    return null;
            }
        }

        public static string GetDatabaseOperation(this Table table, string operation)
        {
            string tableString = GetName(table);
            if (tableString == null)
                return null;
            else
            {
                return operation + tableString;
            }
        }
    }

    /// <summary>
    /// RESOURCE TABLE 필드 상수 <para/>
    /// not null : name, type, ctime
    /// </summary>
    public enum RESOURCE_FIELD
    {
        /// <summary>
        /// int, pk
        /// </summary>
        _ID,
        /// <summary>
        /// text
        /// </summary>
        TITLE,
        /// <summary>
        /// text
        /// </summary>
        CONTENTS,
        /// <summary>
        /// int , TEXT = 0, IMAGE = 1, MOVIE = 2
        /// </summary>
        RES_TYPE,
        /// <summary>
        /// text
        /// </summary>
        EXPANSION,
        /// <summary>
        /// int, not null
        /// </summary>
        CTIME,
        /// <summary>
        /// int
        /// </summary>
        MTIME
    }

    /// <summary>
    /// VIRTUAL TABLE 필드 상수<para/>
    /// not null : resid, type, pos
    /// </summary>
    public enum VIRTUAL_FIELD
    {
        /// <summary>
        /// int, pk
        /// </summary>
        _ID,
        /// <summary>
        /// int , not null
        /// </summary>
        RES_ID,
        /// <summary>
        /// text
        /// </summary>
        NAME,
        /// <summary>
        /// text
        /// </summary>
        PARENT_NAME,
        /// <summary>
        /// int, not null
        /// </summary>
        MODEL_TYPE,

        /// <summary>
        /// real
        /// </summary>
        POS_X,
        /// <summary>
        /// real
        /// </summary>
        POS_Y,
        /// <summary>
        /// real
        /// </summary>
        POS_Z,

        /// <summary>
        /// real
        /// </summary>
        ROTATE_X,
        /// <summary>
        /// real
        /// </summary>
        ROTATE_Y,
        /// <summary>
        /// real
        /// </summary>
        ROTATE_Z,
        /// <summary>
        /// real
        /// </summary>
        ROTATE_W,

        /// <summary>
        /// real
        /// </summary>
        SIZE_X,
        /// <summary>
        /// real
        /// </summary>
        SIZE_Y,
        /// <summary>
        /// real
        /// </summary>
        SIZE_Z

    }

    /// <summary>
    /// AUGMENTED TABLE 필드 상수<para/>
    /// not null : resid
    /// </summary>
    public enum AUGMENTED_FIELD
    {
        /// <summary>
        /// int, pk
        /// </summary>
        _ID,
        /// <summary>
        /// int, not null
        /// </summary>
        RES_ID,
        /// <summary>
        /// real
        /// </summary>
        ALTITUDE,
        /// <summary>
        /// real
        /// </summary>
        LATITUDE,
        /// <summary>
        /// real
        /// </summary>
        LONGITUDE,
        /// <summary>
        /// real
        /// </summary>
        SUPPORT_X,
        /// <summary>
        /// real
        /// </summary>
        SUPPORT_Y,
        /// <summary>
        /// real
        /// </summary>
        SUPPORT_Z
    }

    public enum VR_CONTAINER_FIELD
    {
        /// <summary>
        /// int, pk
        /// </summary>
        _ID,
        /// <summary>
        /// text
        /// </summary>
        NAME,
        /// <summary>
        /// real, default 0
        /// </summary>
        Z_OFFSET, 
        /// <summary>
        /// int, default 0
        /// </summary>
        COUNT
    }
}
