using LitJson;
using System;
using System.Collections.Generic;

namespace AndroidApi.Controller
{
    /// <summary>
    /// Android로 부터 전달받은 Json Message를 해석하는 클래스
    /// </summary>
    internal sealed class JsonInterpreter
    {
        /// <summary>
        /// Json Message를 해석해서, InputCommand를 Operation 배열로 변환한다.
        /// </summary>
        /// <param name="json">InputCommand가 기술된 Json Message</param>
        /// <returns>InputCommand가 해석된 Operation 배열</returns>
        public static List<Operation> InterpretInputCommands(string json)
        {
            JsonData jData = JsonMapper.ToObject(json);

            List<Operation> operations = new List<Operation>(jData.Count);
            int i = 0;
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
        /// <returns>방향 Operation에 대한 정보를 지닌 구조체</returns>
        /// <exception cref="ArgumentException">방향 Operation이 아닌 Operation이 매개변수로 메소드가 호출 된 경우</exception>
        public static Direction InterpretInputDirection(Operation operation)
        {
            if (!operation.IsDirection())
                throw new ArgumentException();

            Direction direction = new Direction();
            direction.Value = operation.Value / Direction.SEPARATION;
            direction.Amount = operation.Value % Direction.SEPARATION;

            return direction;

        }


        public static void InterpretRequestFromAndroid(string json)
        {
            //TODO 안드로이드를 참고하여 작업하기
        }


        /// <summary>
        /// Android에 대해 요청한 결과를 해석한다. 
        /// </summary>
        /// <param name="json">Android에 대해 요청한 결과가 기술된 Json Message</param>
        /// <returns>요청한 결과. RequestResult</returns>
        public static RequestResult InterpretResultFromAndroid(string json)
        {
            RequestResult requestResult = new RequestResult();
            requestResult.Json = JsonMapper.ToObject(json);
            requestResult.Status = (string)requestResult.Json["result"];

            return requestResult;
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
        /// <returns>Field-Value로 구성된 Json 리스트
        /// 
        /// </returns>
        public static List<JsonData> InterpretQueryFromAndroid(JsonData jData)
        {
            List<JsonData> queryResults = new List<JsonData>();

            JsonData queryResultJson = jData["query_result"];
            if (queryResultJson == null || !queryResultJson.IsArray)
                return queryResults;

            for (int i = 0; i < queryResultJson.Count; i++)
                queryResults.Add(queryResultJson[i]);

            return queryResults;
        }
    }

}
