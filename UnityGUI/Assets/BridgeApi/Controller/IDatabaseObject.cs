using LitJson;
using SQLite4Unity3d;
using System;
using System.Collections.Generic;
using System.Text;

namespace BridgeApi.Controller
{
    public interface IDatabaseObject
    {
        /// <summary>
        /// Database에서의 pk, object를 구분하는 요소
        /// </summary>
        [PrimaryKey, AutoIncrement]
        int ID { get; }

        /// <summary>
        /// K-V 배열로 필드를 반환한다. Database 접근 용도로 사용된다.
        /// </summary>
        /// <returns>K-V 배열</returns>
        KeyValuePair<Enum, string>[] ConvertToPairs();

        string ToJSON();

        bool IsInvalid();
    }

    public static class IDatabaseObjectExtend
    {
        public static string StringForm(this IDatabaseObject obj)
        {
            KeyValuePair<Enum, string>[] pairs = obj.ConvertToPairs();

            StringBuilder sb = new StringBuilder();

            sb.AppendFormat("[{0}:", obj.GetType().Name);

            foreach (KeyValuePair<Enum, string> pair in pairs)
            {
                sb.AppendFormat(" {0}={1},", pair.Key, pair.Value);
            }

            sb.Remove(sb.Length - 1, 1);

            return sb.ToString();
        }

        public static string JsonForm(this IDatabaseObject obj)
        {
            KeyValuePair<Enum, string>[] pairs = obj.ConvertToPairs();
            StringBuilder sb = new StringBuilder();
            JsonWriter writer = new JsonWriter(sb);

            writer.WriteObjectStart();
            foreach (KeyValuePair<Enum, string> pair in pairs)
            {
                writer.WritePropertyName(pair.Key.ToString());
                writer.Write(pair.Value);
            }
            writer.WriteObjectEnd();

            return sb.ToString();
        }
    }
}
