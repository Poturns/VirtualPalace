using System;
using BridgeApi.Controller;
using BridgeApi.Controller.Request;
using UnityEngine;
using LitJson;
using System.Threading;
using BridgeApi.Controller.Request.Database;
using System.Collections.Generic;
using MyScript.Objects;

namespace ComputerApi.Controller
{
    public class ComputerUnityBridge : IPlatformBridgeDelegate
    {
        private readonly SQLiteHelper sqliteHelper;

        public ComputerUnityBridge()
        {
            sqliteHelper = new SQLiteHelper("tempDatabase.db");
            sqliteHelper.CreateDB();
        }

        public bool RequestToPlatform(IRequest request, Action<string> callback)
        {
            new Thread(new ThreadStart(() => RequestToPlatform(request.ToJson(), callback))).Start();
            return true;
        }

        public void RespondToPlatform(long id, string jsonResult)
        {
            Debug.LogWarning("==== RespondToPlatform() not implemented, id : " + id + " , result : " + jsonResult);
            return;
        }

        public bool SendSingleMessageToPlatform(string jsonMessage)
        {
            Debug.LogWarning("==== SendSingleMessageToPlatform() not implemented, message : " + jsonMessage);
            return false;
        }



        private void RequestToPlatform(string json, Action<string> callback)
        {
            JsonData jsonData = JsonMapper.ToObject(json);

            foreach (string key in jsonData.Keys)
            {
                JsonData jsonContents = jsonData[key];
                Debug.Log("request : " + key + ", message : " + jsonContents.ToJson());

                switch (key)
                {
                    case SpeechRequestResult.SPEECH_REQUEST_KEY:
                        SendDummySpeechResult(callback);
                        break;

                    case DatabaseConstants.QUERY_UPDATE_VR_BOOKCASES:
                        callback("{result:error}");
                        break;

                    case DatabaseConstants.QUERY_INSERT_VR_ITEMS:
                        callback("{result:error}");
                        break;

                    case DatabaseConstants.QUERY_ALL_VR_ITEMS:
                        callback("{" + key + ":{" + sqliteHelper.QueryAllVRObject() + "}}");
                        break;

                    case DatabaseConstants.QUERY_VR_BOOKCASES:
                        string result = "{" + key + ":{" + sqliteHelper.QueryAllBookCaseObject() + "}}";
                        Debug.Log(result);
                        callback(result);
                        break;

                    case DatabaseConstants.UPDATE + DatabaseConstants.TABLE_VR_CONTAINER:
                        if (ExecuteUpdateBookCaseQuery(jsonContents) > 0)
                            callback("{" + key + ":{result:success}}");
                        else
                            callback("{" + key + ":{result:fail}}");
                        break;

                    case DatabaseConstants.QUERY_NEAR_ITEMS:
                    default:
                        callback("{" + key + "{result:error}}");
                        Debug.LogWarning("=== Not Implemented Request, key : " + key);
                        break;
                }
            }
        }

        private void SendDummySpeechResult(Action<string> callback)
        {
            callback("{" + SpeechRequestResult.SPEECH_REQUEST_KEY + "{mode:'text', result:'Dummy Result Message, Dummy Result Message2'}");
        }

        private int ExecuteUpdateBookCaseQuery(JsonData jsonData)
        {
            BookCaseObject bookcase = BookCaseObject.FromJSON(jsonData["set"]);
            return sqliteHelper.UpdateBookCaseObject(new[] { bookcase });
        }


    }
}
