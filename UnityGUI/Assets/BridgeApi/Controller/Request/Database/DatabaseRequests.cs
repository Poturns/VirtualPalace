using System;
using System.Collections.Generic;
using UnityEngine;

namespace BridgeApi.Controller.Request.Database
{
    public sealed class DatabaseRequests
    {
        public static void VRItemInsert(IPlatformBridge bridge, SaveData saveData, Action<bool> result)
        {
            DatabaseRequestFactory.InsertInto(Table.VR)
                .Values(saveData.ConvertVRMetadataToPairs())
                .SendRequest(bridge, (queryResult) =>
                {
                    Debug.Log("=============== VRItemInsert query result : " + queryResult);
                    result(queryResult.Status.Equals(RequestResult.STATUS_SUCCESS));
                });
        }

        public static void VRItemUpdate(IPlatformBridge bridge, SaveData saveData, Action<bool> result)
        {
            DatabaseRequestFactory.Update(Table.VR)
                .WhereEqual(VIRTUAL_FIELD.NAME, saveData.ObjName)
                .Set(saveData.ConvertVRMetadataToPairs())
                .SendRequest(bridge, (queryResult) =>
                {
                    Debug.Log("=============== VRItemUpdate query result : " + queryResult);
                    result(queryResult.Status.Equals(RequestResult.STATUS_SUCCESS));
                });
        }

        public static void VRItemSelect(IPlatformBridge bridge, Action<List<SaveData>> result)
        {
            DatabaseRequestFactory.Select(Table.VR)
            .SetField(VIRTUAL_FIELD.RES_ID, VIRTUAL_FIELD.NAME, VIRTUAL_FIELD.TYPE,
                        VIRTUAL_FIELD.POS_X, VIRTUAL_FIELD.POS_Y, VIRTUAL_FIELD.POS_Z,
                        VIRTUAL_FIELD.ROTATE_X, VIRTUAL_FIELD.ROTATE_Y, VIRTUAL_FIELD.ROTATE_Z,
                        VIRTUAL_FIELD.SIZE_X, VIRTUAL_FIELD.SIZE_Y, VIRTUAL_FIELD.SIZE_Z,
                        VIRTUAL_FIELD.CONTAINER, VIRTUAL_FIELD.CONT_ORDER, VIRTUAL_FIELD.STYLE)
            .SendRequest(bridge, (queryResult) =>
            {
                Debug.Log("=============== VRItemSelect query result : " + queryResult);
                result(queryResult.QueryData != null ? JsonInterpreter.ParseJsonListToSaveData(queryResult.QueryData) : null);
            });
        }
    }
}
