using MyScript.objects;
using System;
using System.Collections.Generic;
using UnityEngine;

namespace BridgeApi.Controller.Request.Database
{
    public sealed class DatabaseRequests
    {
        public static void QueryBookCaseObjects(IPlatformBridge bridge, Action<List<BookCaseObject>> resultCallback)
        {
            DatabaseRequestFactory.QueryAllBookCaseObjects()
                .SendRequest(bridge, (requestResult) =>
                {
                    Debug.Log("=============== QueryBookCaseObjects result : " + requestResult);
                    resultCallback(JsonInterpreter.ParseJsonListToBookCaseObject(requestResult.QueryData));
                });
        }

        public static void UpdateBookCaseObjects(IPlatformBridge bridge, BookCaseObject data, Action<bool> resultCallback)
        {
            DatabaseRequestFactory.Update(Table.VR_CONTAINER)
                .Set(data.ConvertToPairs())
                .WhereEqual(VR_CONTAINER_FIELD.NAME, data.Name)
                .SendRequest(bridge, (requestResult) =>
                {
                    Debug.Log("=============== UpdateBookCaseObject result : " + requestResult);
                    resultCallback(requestResult.Status.Equals(RequestResult.STATUS_SUCCESS));
                });
        }

        public static void QueryVRObjects(IPlatformBridge bridge, Action<List<VRObject>> resultCallback)
        {
            DatabaseRequestFactory.QueryAllVRObjects()
                .SendRequest(bridge, (requestResult) =>
                {
                    Debug.Log("=============== QueryVRObjects result : " + requestResult);
                    resultCallback(JsonInterpreter.ParseJsonListToVRObject(requestResult.QueryData));
                });
        }

        public static void InsertVRObjects(IPlatformBridge bridge, List<VRObject> dataList, Action<bool> resultCallback)
        {
            DatabaseRequestFactory.InsertVRObjects(dataList)
                .SendRequest(bridge, (requestResult) =>
                {
                    Debug.Log("=============== InsertVRObjects result : " + requestResult);
                    resultCallback(requestResult.Status.Equals(RequestResult.STATUS_SUCCESS));
                });
        }

        public static void UpdateVRObjects(IPlatformBridge bridge, List<VRObject> dataList, Action<bool> resultCallback)
        {
            DatabaseRequestFactory.UpdateVRObjects(dataList)
                .SendRequest(bridge, (requestResult) =>
                {
                    Debug.Log("=============== UpdateVRObjects result : " + requestResult);
                    resultCallback(requestResult.Status.Equals(RequestResult.STATUS_SUCCESS));
                });
        }

        /*
        public static void VRBookCaseItemInsert(IPlatformBridge bridge, SaveDataForBookCase saveData, Action<bool> result)
        {
            DatabaseRequestFactory.InsertInto(Table.VR_CONTAINER)
                .Values(saveData.ConvertToPairs())
                .SendRequest(bridge, (queryResult) =>
                {
                    Debug.Log("=============== VRBookCaseInsert query result : " + queryResult);
                    result(queryResult.Status.Equals(RequestResult.STATUS_SUCCESS));
                });
        }
        

        [Obsolete]
        public static void VRBookCaseItemUpdate(IPlatformBridge bridge, SaveDataForBookCase saveData, Action<bool> result)
        {
            DatabaseRequestFactory.Update(Table.VR_CONTAINER)
                .WhereEqual(VR_CONTAINER_FIELD.NAME, saveData.ObjName)
                .SendRequest(bridge, (queryResult) =>
                {
                    Debug.Log("=============== VRBookCaseInsert query result : " + queryResult);
                    result(queryResult.Status.Equals(RequestResult.STATUS_SUCCESS));
                });
        }

        [Obsolete]
        public static void VRBookCaseItemSelectAll(IPlatformBridge bridge, Action<List<SaveDataForBookCase>> result)
        {
            DatabaseRequestFactory.QueryAllBookCaseObjects()
                .SendRequest(bridge, (queryResult) =>
                {
                    Debug.Log("=============== VRBookCaseSelectAll query result : " + queryResult);
                    result(JsonInterpreter.ParseJsonListToBookCaseData(queryResult.QueryData));
                });
        }

        [Obsolete]
        public static void VRItemInsert(IPlatformBridge bridge, SaveData saveData, Action<bool> result)
        {
            DatabaseRequestFactory.InsertInto(Table.VR)
                .Values(saveData.ConvertToPairs())
                .SendRequest(bridge, (queryResult) =>
                {
                    Debug.Log("=============== VRItemInsert query result : " + queryResult);
                    result(queryResult.Status.Equals(RequestResult.STATUS_SUCCESS));
                });
        }

        [Obsolete]
        public static void VRItemUpdate(IPlatformBridge bridge, SaveData saveData, Action<bool> result)
        {
            DatabaseRequestFactory.Update(Table.VR)
                .WhereEqual(VIRTUAL_FIELD._ID, saveData.Key.ToString())
                .Set(saveData.ConvertToPairs())
                .SendRequest(bridge, (queryResult) =>
                {
                    Debug.Log("=============== VRItemUpdate query result : " + queryResult);
                    result(queryResult.Status.Equals(RequestResult.STATUS_SUCCESS));
                });
        }

        [Obsolete]
        public static void VRItemSelect(IPlatformBridge bridge, Action<List<SaveData>> result)
        {
            DatabaseRequestFactory.Select(Table.VR)
            // query all
            .SendRequest(bridge, (queryResult) =>
            {
                Debug.Log("=============== VRItemSelect query result : " + queryResult);
                result(JsonInterpreter.ParseJsonListToSaveData(queryResult.QueryData));
            });
        }
        */
    }
}
