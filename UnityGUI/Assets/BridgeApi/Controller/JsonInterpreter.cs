using BridgeApi.Controller.Request.Database;
using LitJson;
using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

namespace BridgeApi.Controller
{
    /// <summary>
    /// Android로 부터 전달받은 Json Message를 해석하는 클래스
    /// </summary>
    public sealed class JsonInterpreter
    {
        /// <summary>
        /// Json Message를 해석해서, InputCommand를 Operation 배열로 변환한다.
        /// </summary>
        /// <param name="json">InputCommand가 기술된 Json Message</param>
        /// <returns>InputCommand가 해석된 Operation 배열</returns>
        public static List<Operation> ParseInputCommands(string json)
        {
            JsonData jData = JsonMapper.ToObject(json);

            List<Operation> operations = new List<Operation>(jData.Count);

            foreach (string key in jData.Keys)
            {
                Operation operation = new Operation();
                operation.Type = int.Parse(key);
                operation.Value = (int)jData[key];

                operations.Add(operation);
            }

            return operations;
        }


        /// <summary>
        /// 방향 Operation를 해석해서 방향 정보를 지닌 구조체를 반환한다.
        /// </summary>
        /// <param name="operation">방향 Operation</param>
        /// <returns>방향 Operation에 대한 정보를 지닌 Dictionary, Key는 Direction.DIMENSION_*** 이다.</returns>
        /// <exception cref="ArgumentException">방향 Operation이 아닌 Operation이 매개변수로 메소드가 호출 된 경우</exception>
        public static Dictionary<int, Direction> ParseDirectionAmount(Operation operation)
        {
            if (!operation.IsDirection())
                throw new ArgumentException();

            Dictionary<int, Direction> dictionary = new Dictionary<int, Direction>();
            int direction = operation.Value / Direction.SEPARATION;
            int amount = operation.Value % Direction.SEPARATION;
            if (amount % 10 == 0)
            {
                // 3차원 축 계산
                if (direction > 100)
                {
                    int d_3d = (direction > 500) ? Direction.UPWARD :
                            (direction < 500) ? Direction.DOWNWARD : 0;
                    int a_3d = amount / (10 * 100);

                    if (d_3d > 0)
                    {
                        dictionary.Add(Direction.DIMENSION_3, new Direction() { Amount = a_3d, Value = d_3d });
                    }

                    direction %= 100;
                    amount %= (10 * 100);
                }

                // 2차원 축 계산
                if (direction > 10)
                {
                    int d_2d = (direction > 50) ? Direction.UP :
                            (direction < 50) ? Direction.DOWN : 0;
                    int a_2d = amount / (10 * 10);

                    if (d_2d > 0)
                    {
                        dictionary.Add(Direction.DIMENSION_2, new Direction() { Amount = a_2d, Value = d_2d });
                    }

                    direction %= 10;
                    amount %= (10 * 10);
                }

                // 1차원 축 계산
                int d_1d = (direction > 5) ? Direction.RIGHT :
                        (direction < 5) ? Direction.LEFT : 0;
                int a_1d = amount / (10 * 1);

                if (d_1d > 0)
                {
                    dictionary.Add(Direction.DIMENSION_1, new Direction() { Amount = a_1d, Value = d_1d });
                }

            }
            else
            {
                dictionary.Add(Direction.DIMENSION_NONE, new Direction() { Amount = amount / 10, Value = direction });
            }

            return dictionary;
        }


        public static void ParseRequestFromPlatform(string json)
        {
            //TODO 안드로이드를 참고하여 작업하기
        }


        /// <summary>
        /// 기저 Platform에 요청한 결과를 해석한다. 
        /// </summary>
        /// <param name="json">기저 Platform에 요청한 결과가 기술된 Json Message</param>
        /// <returns>요청들에 대한 결과</returns>
        public static List<RequestResult> ParseResultFromPlatform(string json)
        {
            //Debug.Log(json);
            JsonData jData = JsonMapper.ToObject(json);
            List<RequestResult> list = new List<RequestResult>(jData.Count);

            foreach (string key in jData.Keys)
            {
                list.Add(new RequestResult { RequestName = key, Status = (string)jData[key][RequestResult.RESULT] });
            }

            return list;
        }

        /// <summary>
        /// Json으로 표현된 Database에 질의한 결과를 해석한다.
        /// <para/>
        /// 그 결과는  Field-Value로 구성된 Json 리스트이다.
        /// <para/>
        /// 예)<para/>
        /// index 0 : { "_id" : 1, "res_id" : 1, .... }<para/>
        ///  index 1 : { "_id" : 2, "res_id" : 2, .... }<para/>
        /// ....
        /// 
        /// </summary>
        /// <param name="json">Json으로 표현된 Database 질의 결과</param>
        /// <param name="requestKey">요청 Key</param>
        /// <returns>Field-Value로 구성된 Json 리스트</returns>
        public static QueryRequestResult ParseQueryFromPlatform(string json, string requestKey)
        {
            //Debug.Log(json);
            JsonData jData = JsonMapper.ToObject(json);
            List<JsonData> queryResults = new List<JsonData>();

            QueryRequestResult result = new QueryRequestResult();
            result.RequestName = requestKey;
            result.Status = (string)jData[requestKey][RequestResult.RESULT];

            if (JsonDataContainsKey(jData, DatabaseConstants.QUERY_RESULT))
            {
                JsonData queryResultJson = jData[DatabaseConstants.QUERY_RESULT];
                if (queryResultJson != null && queryResultJson.IsArray)
                    for (int i = 0; i < queryResultJson.Count; i++)
                        queryResults.Add(queryResultJson[i]);
            }
            else
            {
                result.QueryData = null;
            }

            return result;
        }

        /// <summary>
        /// Json 내부에 해당 key값이 존재하는지 확인한다.
        /// </summary>
        /// <param name="json">검사할 json</param>
        /// <param name="key">확인할 key</param>
        /// <returns>key가 존재하는지 여부</returns>
        private static bool JsonDataContainsKey(JsonData json, string key)
        {
            if (json == null)
                return false;
            if (!json.IsObject)
            {
                return false;
            }

            IDictionary tdictionary = json as IDictionary;
            if (tdictionary == null)
                return false;

            return tdictionary.Contains(key);
        }

        /// <summary>
        /// 음성인식 요청한 결과를 해석한다. 
        /// </summary>
        /// <param name="json">기저 Platform에 요청한 결과가 기술된 Json Message</param>
        /// <returns>음성인식 데이터</returns>
        public static SpeechRequestResult ParseSpeechResultFromPlatform(string json)
        {
            Debug.Log(json);
            JsonData jData = JsonMapper.ToObject(json);
            SpeechRequestResult result = new SpeechRequestResult();

            result.RequestName = SpeechRequestResult.SPEECH_REQUEST_KEY;
            result.Status = (string)jData[RequestResult.RESULT];
            result.Speech = (string)jData[SpeechRequestResult.SPEECH_REQUEST_KEY];

            return result;
        }
    }

}
