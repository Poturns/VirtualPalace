using LitJson;
using System;
using static AndroidApi.Controller.Controller;

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
        public static Operation[] InterpretInputCommands(string json)
        {
            JsonData jData = JsonMapper.ToObject(json);

            Operation[] operations = new Operation[jData.Count];
            int i = 0;
            foreach (string key in jData.Keys)
            {
                Operation operation = new Operation();
                operation.Type = int.Parse(key);
                operation.Value = (int)jData[key];
                
                operations[i++] = operation;
            }

            return operations;
        }

        /// <summary>
        /// 방향 Operation를 해석해서 
        /// </summary>
        /// <param name="operation">방향 Operation</param>
        /// <returns>방향 Operation에 대한 정보를 </returns>
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

        }


        /// <summary>
        /// Android에 대해 요청한 결과를 해석한다. 
        /// </summary>
        /// <param name="json">Android에 대해 요청한 결과가 기술된 Json Message</param>
        /// <returns>요청한 결과. RequestResult</returns>
        public static RequestResult InterpretResultFromAndroid(string json)
        {
            switch ((string)JsonMapper.ToObject(json)["result"]){
                case "success":
                    return RequestResult.Success;
                case "fail":
                    return RequestResult.Fail;
                case "error":
                default:
                    return RequestResult.Error;
            }
        }

    }

}
