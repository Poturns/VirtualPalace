using LitJson;
using System;
using System.Collections.Generic;
using System.Text;
using UnityEngine;

namespace AndroidApi.Controller.Request.Database
{
    /// <summary>
    /// Database관련 처리를 요청하는 IRequest
    /// </summary>
    public interface IDatabaseRequest : IRequest
    {

        /// <summary>
        /// 요청을 전송한다.
        /// </summary>
        /// <param name="callback">요청에 대한 응답을 전달받을 Callback</param>
        void SendRequest(Action<QueryRequestResult> callback);
    }

    /// <summary>
    /// 
    /// </summary>
    public class DatabaseRequestFactory
    {
        #region Factory Method

        public static IDatabaseRequest QueryAllVRItems()
        {
            return new SpecialQueryRequest(DatabaseConstants.QUERY_ALL_VR_ITEMS);
        }

        public static IDatabaseRequest QueryNearItems()
        {
            return new SpecialQueryRequest(DatabaseConstants.QUERY_NEAR_ITEMS);
        }

        /// <summary>
        /// Select 쿼리 요청을 전송할 객체를 생성한다.
        /// </summary>
        /// <param name="place"> Select 쿼리 요청을 할 테이블</param>
        /// <returns> Select 쿼리 요청을 전송할 객체</returns>
        public static ISelect Select(Place place)
        {
            return new SelectImpl(place);
        }

        /// <summary>
        /// Insert 쿼리 요청을 전송할 객체를 생성한다.
        /// </summary>
        /// <param name="place"> Insert 쿼리 요청을 할 테이블</param>
        /// <returns> Insert 쿼리 요청을 전송할 객체</returns>
        public static IInsert InsertInto(Place place)
        {
            return new InsertImpl(place);
        }

        /// <summary>
        /// Update 쿼리 요청을 전송할 객체를 생성한다.
        /// </summary>
        /// <param name="place"> Update 쿼리 요청을 할 테이블</param>
        /// <returns> Delete 쿼리 요청을 전송할 객체</returns>
        public static IUpdate Update(Place place)
        {
            return new UpdateImpl(place);
        }

        /// <summary>
        /// Delete 쿼리 요청을 전송할 객체를 생성한다.
        /// </summary>
        /// <param name="place"> Delete 쿼리 요청을 할 테이블</param>
        /// <returns> Delete 쿼리 요청을 전송할 객체</returns>
        public static IDelete Delete(Place place)
        {
            return new DeleteImpl(place);
        }
        #endregion

        #region Object Impl
        private class SpecialQueryRequest : IDatabaseRequest
        {
            private readonly string OPERATION;
            private JsonWriter writer;
            private StringBuilder sb;
            public SpecialQueryRequest(string operation)
            {
                OPERATION = operation;
                writer = new JsonWriter(sb = new StringBuilder());
                writer.WriteObjectStart();
                writer.WritePropertyName(OPERATION);
                writer.Write(OPERATION);
                writer.WriteObjectEnd();
            }

            public void SendRequest(Action<QueryRequestResult> callback)
            {
                AndroidUnityBridge.GetInstance().RequestToAndroid(this, (requestResult) =>
                {
                    callback(JsonInterpreter.ParseQueryFromAndroid(requestResult, OPERATION));
                });
            }

            public string ToJson()
            {
                return sb.ToString();
            }
        }

        protected class QueryRequest : IDatabaseRequest
        {
            protected QueryBuilder queryBuilder;
            protected const int OPERATION_SELECT = 0;
            protected const int OPERATION_UPDATE = 1;
            protected const int OPERATION_INSERT = 2;
            protected const int OPERATION_DELETE = 3;
            private string operation;

            protected QueryRequest(Place place, int operationCode)
            {
                operation = null;
                switch (operationCode)
                {
                    default:
                    case OPERATION_SELECT:
                        switch (place)
                        {
                            case Place.AR:
                                operation = DatabaseConstants.SELECT_AR;
                                break;
                            case Place.RES:
                                operation = DatabaseConstants.SELECT_RES;
                                break;
                            case Place.VR:
                                operation = DatabaseConstants.SELECT_VR;
                                break;
                        }
                        break;
                    case OPERATION_UPDATE:
                        switch (place)
                        {
                            case Place.AR:
                                operation = DatabaseConstants.UPDATE_AR;
                                break;
                            case Place.RES:
                                operation = DatabaseConstants.UPDATE_RES;
                                break;
                            case Place.VR:
                                operation = DatabaseConstants.UPDATE_VR;
                                break;
                        }
                        break;
                    case OPERATION_INSERT:
                        switch (place)
                        {
                            case Place.AR:
                                operation = DatabaseConstants.INSERT_AR;
                                break;
                            case Place.RES:
                                operation = DatabaseConstants.INSERT_RES;
                                break;
                            case Place.VR:
                                operation = DatabaseConstants.INSERT_VR;
                                break;
                        }
                        break;
                    case OPERATION_DELETE:
                        switch (place)
                        {
                            case Place.AR:
                                operation = DatabaseConstants.DELETE_AR;
                                break;
                            case Place.RES:
                                operation = DatabaseConstants.DELETE_RES;
                                break;
                            case Place.VR:
                                operation = DatabaseConstants.DELETE_VR;
                                break;
                        }
                        break;
                }

                queryBuilder = new QueryBuilder(operation);
                queryBuilder.StartWrite();
            }

            public string ToJson()
            {
                return queryBuilder.ToJsonString();
            }

            public void SendRequest(Action<QueryRequestResult> callback)
            {
                queryBuilder.EndWrite();
                Debug.Log(queryBuilder.ToJsonString());

                try {
                    AndroidUnityBridge.GetInstance().RequestToAndroid(this, (requestResult) =>
                    {
                        callback(JsonInterpreter.ParseQueryFromAndroid(requestResult, operation));
                    });
                }catch(Exception e)
                {
                }

            }
        }


        private class SelectImpl : QueryRequest, ISelect
        {
            public SelectImpl(Place place) : base(place, OPERATION_SELECT)
            {
            }

            public ISelect SetField(params Enum[] fields)
            {
                queryBuilder.SetField(fields);
                return this;
            }

            public ISelect WhereEqual(Enum field, string value)
            {
                queryBuilder.WhereEqual(field.ToString(), value);
                return this;
            }

            public ISelect WhereNotEqual(Enum field, string value)
            {
                queryBuilder.WhereNot(field.ToString(), value);
                return this;
            }

            public ISelect WhereBetween(Enum field, string fromValue, string toValue)
            {
                queryBuilder.WhereBetween(field.ToString(), fromValue, toValue);
                return this;
            }

            public ISelect WhereGreater(Enum field, string value, bool allowEqual = true)
            {
                queryBuilder.WhereGreater(field.ToString(), value, allowEqual);
                return this;
            }

            public ISelect WhereSmaller(Enum field, string value, bool allowEqual = true)
            {
                queryBuilder.WhereSmaller(field.ToString(), value, allowEqual);
                return this;
            }

            public ISelect WhereLike(Enum field, string value)
            {
                queryBuilder.WhereLike(field.ToString(), value);
                return this;
            }
        }

        private class InsertImpl : QueryRequest, IInsert
        {
            public InsertImpl(Place place) : base(place, OPERATION_INSERT)
            {
            }

            public IInsert Values(params KeyValuePair<Enum, string>[] pairs)
            {
                queryBuilder.SetField(pairs);
                return this;
            }

        }

        private class UpdateImpl : QueryRequest, IUpdate
        {

            public UpdateImpl(Place place) : base(place, OPERATION_UPDATE)
            {
            }

            public IUpdate Set(params KeyValuePair<Enum, string>[] pairs)
            {
                queryBuilder.SetField(pairs);
                return this;
            }

            public IUpdate WhereEqual(Enum field, string value)
            {
                queryBuilder.WhereEqual(field.ToString(), value);
                return this;
            }

            public IUpdate WhereNotEqual(Enum field, string value)
            {
                queryBuilder.WhereNot(field.ToString(), value);
                return this;
            }

            public IUpdate WhereBetween(Enum field, string fromValue, string toValue)
            {
                queryBuilder.WhereBetween(field.ToString(), fromValue, toValue);
                return this;
            }

            public IUpdate WhereGreater(Enum field, string value, bool allowEqual = true)
            {
                queryBuilder.WhereGreater(field.ToString(), value, allowEqual);
                return this;
            }

            public IUpdate WhereSmaller(Enum field, string value, bool allowEqual = true)
            {
                queryBuilder.WhereSmaller(field.ToString(), value, allowEqual);
                return this;
            }

            public IUpdate WhereLike(Enum field, string value)
            {
                queryBuilder.WhereLike(field.ToString(), value);
                return this;
            }
        }

        private class DeleteImpl : QueryRequest, IDelete
        {
            public DeleteImpl(Place place) : base(place, OPERATION_DELETE)
            {
            }

            public IDelete WhereEqual(Enum field, string value)
            {
                queryBuilder.WhereEqual(field.ToString(), value);
                return this;
            }

            public IDelete WhereNotEqual(Enum field, string value)
            {
                queryBuilder.WhereNot(field.ToString(), value);
                return this;
            }

            public IDelete WhereBetween(Enum field, string fromValue, string toValue)
            {
                queryBuilder.WhereBetween(field.ToString(), fromValue, toValue);
                return this;
            }

            public IDelete WhereGreater(Enum field, string value, bool allowEqual = true)
            {
                queryBuilder.WhereGreater(field.ToString(), value, allowEqual);
                return this;
            }

            public IDelete WhereSmaller(Enum field, string value, bool allowEqual = true)
            {
                queryBuilder.WhereSmaller(field.ToString(), value, allowEqual);
                return this;
            }

            public IDelete WhereLike(Enum field, string value)
            {
                queryBuilder.WhereLike(field.ToString(), value);
                return this;
            }
        }

#endregion Object Impl


        protected class QueryBuilder
        {
            private readonly string OPERATION;

            private JsonWriter writer;
            private StringBuilder sb;
            public QueryBuilder(string operation)
            {
                OPERATION = operation;
                writer = new JsonWriter(sb = new StringBuilder());
            }

            /// <summary>
            /// JSON 작성을 시작하고, Database operation을 기록한다. 작성이 끝나면 명시적으로 EndWrite()를 호출해주어야 한다. <para/>
            /// Database operation : select_ar, delete_vr 등등
            /// </summary>
            public void StartWrite()
            {
                writer.WriteObjectStart();
                writer.WritePropertyName(OPERATION);
                writer.WriteObjectStart();
            }

            /// <summary>
            /// JSON 작성을 종료한다.
            /// </summary>
            public void EndWrite()
            {
                writer.WriteObjectEnd();
                writer.WriteObjectEnd();
            }

            /// <summary>
            /// Select 명령에서 사용할 필드를 기록한다.
            /// 
            /// "set" : [
            ///          "name",
            ///          "type",
            ///          "description"
            /// ]
            /// </summary>
            /// <param name="fields">필드들</param>
            public void SetField(params Enum[] fields)
            {
                writer.WritePropertyName(DatabaseConstants.OPERATION_SET);

                writer.WriteArrayStart();
                foreach (Enum field in fields)
                    writer.Write(field.ToString().ToLowerInvariant());
                writer.WriteArrayEnd();


            }

            /// <summary>
            /// Select 이외의 명령에서 사용할 필드를 기록한다.
            ///  "set" : {
            ///          "name" : v1,
            ///          "type" : v2,
            ///          "description" : v3
            ///
            /// }
            /// </summary>
            /// <param name="pairs">필드들</param>
            public void SetField(params KeyValuePair<Enum, string>[] pairs)
            {
                writer.WritePropertyName(DatabaseConstants.OPERATION_SET);

                writer.WriteObjectStart();
                foreach (KeyValuePair<Enum, string> pair in pairs)
                {
                    writer.WritePropertyName(pair.Key.ToString().ToLowerInvariant());
                    writer.Write(pair.Value);
                }
                writer.WriteObjectEnd();

            }

            public void WhereEqual(string field, string value)
            {
                WriteJsonObjectInside(writer, DatabaseConstants.OPERATION_WHERE, field, value);
            }

            public void WhereNot(string field, string value)
            {
                WriteJsonObjectInside(writer, DatabaseConstants.OPERATION_WHERE_NOT, field, value);
            }
            public void WhereGreater(string field, string value, bool allowEqual)
            {
                WriteAllowEqualJson(writer, DatabaseConstants.OPERATION_WHERE_GREATER, field, value, allowEqual);
            }
            public void WhereSmaller(string field, string value, bool allowEqual)
            {
                WriteAllowEqualJson(writer, DatabaseConstants.OPERATION_WHERE_SMALLER, field, value, allowEqual);
            }
            public void WhereBetween(string field, string fromValue, string toValue)
            {
                WriteJsonObjectInside(writer, DatabaseConstants.OPERATION_WHERE_FROM, field, fromValue);
                WriteJsonObjectInside(writer, DatabaseConstants.OPERATION_WHERE_TO, field, toValue);
            }
            public void WhereLike(string field, string value)
            {
                WriteJsonObjectInside(writer, DatabaseConstants.OPERATION_WHERE_LIKE, field, value);
            }

            /// <summary>
            /// AllowEqual이 포함된 쿼리를 작성한다. (WhereGreater , WhereSmaller)
            /// </summary>
            private static void WriteAllowEqualJson(JsonWriter writer, string operation, string field, string value, bool allowEqual)
            {
                writer.WritePropertyName(operation);
                WriteJsonObject(writer, field, value);
                WriteJsonObject(writer, DatabaseConstants.OPERATION_ALLOW_EQUAL, allowEqual);
            }
            /// <summary>
            ///  일반 쿼리를 작성한다.
            ///  "outPropertyName" : {
            ///           "innerPropertyName" : value
            /// }
            /// </summary>
            private static void WriteJsonObjectInside(JsonWriter writer, string outPropertyName, string innerPropertyName, string value)
            {
                writer.WritePropertyName(outPropertyName);
                WriteJsonObject(writer, innerPropertyName, value);
            }

            /// <summary>
            ///  일반 쿼리를 작성한다.
            /// {
            ///           "propertyName" : value
            /// }
            /// </summary>
            private static void WriteJsonObject(JsonWriter writer, string propertyName, string value)
            {
                writer.WriteObjectStart();
                writer.WritePropertyName(propertyName);
                writer.Write(value);
                writer.WriteObjectEnd();
            }

            /// <summary>
            ///  일반 쿼리를 작성한다.
            /// {
            ///           "propertyName" : value
            /// }
            /// </summary>
            private static void WriteJsonObject(JsonWriter writer, string propertyName, bool value)
            {
                writer.WriteObjectStart();
                writer.WritePropertyName(propertyName);
                writer.Write(value);
                writer.WriteObjectEnd();
            }

            public string ToJsonString()
            {
                return sb.ToString();
            }
        }

    }

#region Interface
    public interface ISelect : IDatabaseRequest
    {
        ISelect SetField(params Enum[] fields);
        ISelect WhereEqual(Enum field, string value);
        ISelect WhereNotEqual(Enum field, string value);
        ISelect WhereGreater(Enum field, string value, bool allowEqual);
        ISelect WhereSmaller(Enum field, string value, bool allowEqual);
        ISelect WhereBetween(Enum field, string fromValue, string toValue);
        ISelect WhereLike(Enum field, string value);
    }

    public interface IInsert : IDatabaseRequest
    {
        IInsert Values(params KeyValuePair<Enum, string>[] pairs);
    }

    public interface IUpdate : IDatabaseRequest
    {
        IUpdate Set(params KeyValuePair<Enum, string>[] pairs);
        IUpdate WhereEqual(Enum field, string value);
        IUpdate WhereNotEqual(Enum field, string value);
        IUpdate WhereGreater(Enum field, string value, bool allowEqual);
        IUpdate WhereSmaller(Enum field, string value, bool allowEqual);
        IUpdate WhereBetween(Enum field, string fromValue, string toValue);
        IUpdate WhereLike(Enum field, string value);
    }

    public interface IDelete : IDatabaseRequest
    {
        IDelete WhereEqual(Enum field, string value);
        IDelete WhereNotEqual(Enum field, string value);
        IDelete WhereGreater(Enum field, string value, bool allowEqual);
        IDelete WhereSmaller(Enum field, string value, bool allowEqual);
        IDelete WhereBetween(Enum field, string fromValue, string toValue);
        IDelete WhereLike(Enum field, string value);
    }

    public interface IValueInsert<T>
    {
        IValueInsert<T> AddValue(params KeyValuePair<Enum, string>[] pairs);
        T End();
    }
#endregion Interface

    public class DatabaseConstants
    {
        ///<summary>
        /// 현재 위치에서 일정 범위 내 존재하는 데이터를 찾는다.
        ///</summary>
        public const string QUERY_NEAR_ITEMS = "query_near_items";
        ///<summary>
        /// VR 아이템에 렌더링할 아이템 데이터를 찾는다.
        ///</summary>
        public const string QUERY_ALL_VR_ITEMS = "query_all_vr_items";


        ///<summary>
        /// (응답 반환시) 쿼리 결과 KEY
        ///<para/>
        /// Ex)<para/>
        /// {<para/>
        ///     "query_result" : [<para/>
        ///          { "_id" : 1, "res_id" : 1, .... },<para/>
        ///          { "_id" : 2, "res_id" : 2, .... },<para/>
        ///     ]<para/>
        /// }<para/>
        ///</summary>
        public const string QUERY_RESULT = "query_result";

        ///<summary>
        /// VR 테이블 조회 명령.
        ///</summary>
        public const string SELECT_VR = "select_vr";
        ///<summary>
        /// AR 테이블 조회 명령.
        ///</summary>
        public const string SELECT_AR = "select_ar";
        ///<summary>
        /// Resource 테이블 조회 명령.
        ///</summary>
        public const string SELECT_RES = "select_res";
        ///<summary>
        /// VR 테이블 데이터 삽입 명령.
        ///</summary>
        public const string INSERT_VR = "insert_vr";
        ///<summary>
        /// AR 테이블 데이터 삽입 명령.
        ///</summary>
        public const string INSERT_AR = "insert_ar";
        ///<summary>
        /// Resource 테이블 데이터 삽입 명령.
        ///</summary>
        public const string INSERT_RES = "insert_res";
        ///<summary>
        /// VR 테이블 데이터 수정 명령.
        ///</summary>
        public const string UPDATE_VR = "update_vr";
        ///<summary>
        /// AR 테이블 데이터 수정 명령.
        ///</summary>
        public const string UPDATE_AR = "update_ar";
        ///<summary>
        /// Resource 테이블 데이터 수정 명령.
        ///</summary>
        public const string UPDATE_RES = "update_res";
        ///<summary>
        /// VR 테이블 데이터 삭제 명령.
        ///</summary>
        public const string DELETE_VR = "delete_vr";
        ///<summary>
        /// AR 테이블 데이터 삭제 명령.
        ///</summary>
        public const string DELETE_AR = "delete_ar";
        ///<summary>
        /// Resource 테이블 데이터 삭제 명령.
        ///</summary>
        public const string DELETE_RES = "delete_res";

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
#region Database Property

    /// <summary>
    /// Database에서 조작할 Table
    /// </summary>
    public enum Place
    {
        VR, AR, RES
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
        /// text, not null
        /// </summary>
        NAME,
        /// <summary>
        /// text, not null
        /// </summary>
        TYPE,
        /// <summary>
        /// text
        /// </summary>
        CATEGORY,
        /// <summary>
        /// text
        /// </summary>
        ARCHIVE_PATH,
        /// <summary>
        /// text
        /// </summary>
        ARCHIVE_KEY,
        /// <summary>
        /// text
        /// </summary>
        DRIVE_PATH,
        /// <summary>
        /// text
        /// </summary>
        DRIVE_KEY,
        /// <summary>
        /// text
        /// </summary>
        THUMBNAIL_PATH,
        /// <summary>
        /// text
        /// </summary>
        DESCRIPTION,
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
        /// text, not null
        /// </summary>
        TYPE,
        /// <summary>
        /// real, not null
        /// </summary>
        POS_X,
        /// <summary>
        /// real, not null
        /// </summary>
        POS_Y,
        /// <summary>
        /// real, not null
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
        /// int
        /// </summary>
        CONTAINER,
        /// <summary>
        /// int
        /// </summary>
        CONT_ORDER,
        /// <summary>
        /// text
        /// </summary>
        STYLE

    }

    /// <summary>
    /// AUGMENTED TABLE 필드 상수<para/>
    /// not null : resid, alt, lat, long
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
        /// real, not null
        /// </summary>
        ALTITUDE,
        /// <summary>
        /// real, not null
        /// </summary>
        LATITUDE,
        /// <summary>
        /// real, not null
        /// </summary>
        LONGITUDE

    }

#endregion Database Property
}
