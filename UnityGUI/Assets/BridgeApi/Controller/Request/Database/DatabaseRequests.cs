using System;
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
    }
}
