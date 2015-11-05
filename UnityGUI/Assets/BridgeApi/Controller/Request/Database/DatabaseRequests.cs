using System;
using System.Collections.Generic;
using UnityEngine;

namespace BridgeApi.Controller.Request.Database
{
    public sealed class DatabaseRequests
    {
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
        */

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

        public static void VRBookCaseItemSelectAll(IPlatformBridge bridge, Action<List<SaveDataForBookCase>> result)
        {
            DatabaseRequestFactory.QueryBookCaseItems()
                .SendRequest(bridge, (queryResult) =>
                {
                    Debug.Log("=============== VRBookCaseSelectAll query result : " + queryResult);
                    result(JsonInterpreter.ParseJsonListToBookCaseData(queryResult.QueryData));
                });
        }

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
    }
}
