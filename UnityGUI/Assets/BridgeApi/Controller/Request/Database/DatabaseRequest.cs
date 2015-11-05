using LitJson;
using MyScript.objects;
using System;
using System.Collections.Generic;
using System.Text;
using UnityEngine;

namespace BridgeApi.Controller.Request.Database
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
        void SendRequest(IPlatformBridge bridge, Action<QueryRequestResult> callback);
    }

    /// <summary>
    /// 
    /// </summary>
    public class DatabaseRequestFactory
    {
        #region Factory Method

        public static IDatabaseRequest QueryAllVRObjects()
        {
            return new SpecialQueryRequest(DatabaseConstants.QUERY_ALL_VR_ITEMS);
        }

        public static IDatabaseRequest QueryNearItems()
        {
            return new SpecialQueryRequest(DatabaseConstants.QUERY_NEAR_ITEMS);
        }

        public static IDatabaseRequest QueryAllBookCaseObjects()
        {
            return new SpecialQueryRequest(DatabaseConstants.QUERY_VR_BOOKCASES);
        }

        public static IDatabaseRequest InsertVRObjects(List<VRObject> list)
        {
            return new DatabaseModifyRequest<VRObject>(DatabaseConstants.QUERY_INSERT_VR_ITEMS, list);
        }

        public static IDatabaseRequest UpdateVRObjects(List<VRObject> list)
        {
            return new DatabaseModifyRequest<VRObject>(DatabaseConstants.QUERY_UPDATE_VR_ITEMS, list);
        }

        /// <summary>
        /// Select 쿼리 요청을 전송할 객체를 생성한다.
        /// </summary>
        /// <param name="place"> Select 쿼리 요청을 할 테이블</param>
        /// <returns> Select 쿼리 요청을 전송할 객체</returns>
        public static ISelect Select(Table place)
        {
            return new SelectImpl(place);
        }

        /// <summary>
        /// Insert 쿼리 요청을 전송할 객체를 생성한다.
        /// </summary>
        /// <param name="place"> Insert 쿼리 요청을 할 테이블</param>
        /// <returns> Insert 쿼리 요청을 전송할 객체</returns>
        public static IInsert InsertInto(Table place)
        {
            return new InsertImpl(place);
        }

        /// <summary>
        /// Update 쿼리 요청을 전송할 객체를 생성한다.
        /// </summary>
        /// <param name="place"> Update 쿼리 요청을 할 테이블</param>
        /// <returns> Delete 쿼리 요청을 전송할 객체</returns>
        public static IUpdate Update(Table place)
        {
            return new UpdateImpl(place);
        }

        /// <summary>
        /// Delete 쿼리 요청을 전송할 객체를 생성한다.
        /// </summary>
        /// <param name="place"> Delete 쿼리 요청을 할 테이블</param>
        /// <returns> Delete 쿼리 요청을 전송할 객체</returns>
        public static IDelete Delete(Table place)
        {
            return new DeleteImpl(place);
        }
        #endregion

        #region Object Impl
        private class DatabaseModifyRequest<T> : IDatabaseRequest where T : IDatabaseObject
        {
            private readonly string OPERATION;

            private JsonWriter writer;
            private StringBuilder sb;
            private List<T> items;

            public DatabaseModifyRequest(string operation, List<T> modifyItems)
            {
                OPERATION = operation;
                items = modifyItems;

                writer = new JsonWriter(sb = new StringBuilder());
                writer.WriteObjectStart();
                writer.WritePropertyName(OPERATION);

            }

            private void WriteItemsToJson()
            {
                writer.WriteArrayStart();

                foreach (T item in items)
                {
                    ConvertItemToJson(item);
                }

                writer.WriteArrayEnd();
            }

            private void ConvertItemToJson(T item)
            {
                KeyValuePair<Enum, string>[] array = item.ConvertToPairs();
                writer.WriteObjectStart();
                foreach (KeyValuePair<Enum, string> pair in array)
                {
                    writer.WritePropertyName(pair.Key.ToString());
                    writer.Write(pair.Value);
                }
                writer.WriteObjectEnd();
            }

            public void SendRequest(IPlatformBridge bridge, Action<QueryRequestResult> callback)
            {
                WriteItemsToJson();
                writer.WriteObjectEnd();

                Debug.Log("============= DatabaseModifyRequest : " + OPERATION + "\n" + sb.ToString());

                bridge.RequestToPlatform(this, (requestResult) =>
                {
                    callback(JsonInterpreter.ParseQueryFromPlatform(requestResult, OPERATION));
                });
            }

            public string ToJson()
            {
                return sb.ToString();
            }
        }


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

            public void SendRequest(IPlatformBridge bridge, Action<QueryRequestResult> callback)
            {
                bridge.RequestToPlatform(this, (requestResult) =>
                {
                    callback(JsonInterpreter.ParseQueryFromPlatform(requestResult, OPERATION));
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

            protected QueryRequest(Table place, int operationCode)
            {
                operation = null;
                switch (operationCode)
                {
                    default:
                    case OPERATION_SELECT:
                        operation = place.GetDatabaseOperation(DatabaseConstants.SELECT);
                        break;
                    case OPERATION_UPDATE:
                        operation = place.GetDatabaseOperation(DatabaseConstants.UPDATE);

                        break;
                    case OPERATION_INSERT:
                        operation = place.GetDatabaseOperation(DatabaseConstants.INSERT);
                        break;
                    case OPERATION_DELETE:
                        operation = place.GetDatabaseOperation(DatabaseConstants.DELETE);
                        break;
                }

                if (operation == null)
                    throw new InvalidOperationException("Unknown Table");

                queryBuilder = new QueryBuilder(operation);
                queryBuilder.StartWrite();
            }

            public string ToJson()
            {
                return queryBuilder.ToJsonString();
            }

            public void SendRequest(IPlatformBridge bridge, Action<QueryRequestResult> callback)
            {
                queryBuilder.EndWrite();
                Debug.Log(queryBuilder.ToJsonString());

                try
                {
                    bridge.RequestToPlatform(this, (requestResult) =>
                    {
                        callback(JsonInterpreter.ParseQueryFromPlatform(requestResult, operation));
                    });
                }
                catch (Exception e)
                {
                    Debug.LogException(e);
                }

            }
        }


        private class SelectImpl : QueryRequest, ISelect
        {
            public SelectImpl(Table place) : base(place, OPERATION_SELECT)
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
            public InsertImpl(Table place) : base(place, OPERATION_INSERT)
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

            public UpdateImpl(Table place) : base(place, OPERATION_UPDATE)
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
            public DeleteImpl(Table place) : base(place, OPERATION_DELETE)
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

}
