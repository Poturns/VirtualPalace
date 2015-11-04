using LitJson;
using System.Collections.Generic;

namespace BridgeApi.Controller
{
   
    /// <summary>
    /// Input Module을 통해 전송되는 명령
    /// </summary>
    public struct Operation
    {

        #region Constants
        /// <summary>
        /// 아무것도 아닌 명령
        /// </summary>
        public const int NONE = 0x0;
        /// <summary>
        /// (1) 결정 기능 : 선택한다.    >> OK / Confirm 하드웨어 키를 눌렀을 때와 동일.
        /// </summary>
        public const int SELECT = 0x1;

        /// <summary>
        /// (1) 결정 기능 : 취소한다.    >> BACK 하드웨어 키를 눌렀을 때와 동일.
        /// </summary>
        public const int CANCEL = 0x2;

        /// <summary>
        /// (2) 기본 기능 : 전진한다.
        /// </summary>
        public const int GO = 0x100;

        /// <summary>
        /// (2) 기본 기능 : 회전한다.
        /// </summary>
        public const int TURN = 0x200;

        /// <summary>
        /// (2) 기본 기능 : Focus 를 이동한다.
        /// </summary>
        public const int FOCUS = 0x400;

        /// <summary>
        /// (2) 기본 기능 : 화면 확대/축소 전환을 한다.
        /// </summary>
        public const int ZOOM = 0x800;

        /// <summary>
        /// (3) 특수 기능 : OPERATION : 숨겨진 기능을 수행한다. (LongClick, 오른쪽클릭 등의 효과)
        /// </summary>
        public const int DEEP = 0x1000;

        /// <summary>
        /// (3) 특수 기능 : 검색한다.
        /// </summary>
        public const int SEARCH = 0x2000;

        /// <summary>
        /// (3) 특수 기능 : 설정/수정한다.
        /// </summary>
        public const int CONFIG = 0x4000;

        /// <summary>
        /// (3) 특수 기능 : 모드를 전환한다.
        /// </summary>
        public const int SWITCH_MODE = 0x8000;

        /// <summary>
        /// (3) 특수 기능 : 종료한다.
        /// </summary>
        public const int TERMINATE = 0x10000;
        #endregion Constants


        /// <summary>
        /// 명령의 종류
        /// </summary>
        public int Type;

        /// <summary>
        /// 명령 값
        /// </summary>
        public int Value;

        /// <summary>
        /// 명령이 방향과 관련된 명령인지 확인한다.
        /// </summary>
        /// <returns>방향과 관련된 명령 (GO, TURN, FOCUS, ZOOM)일 경우 true</returns>
        public bool IsDirection
        {
            get { return Type == GO || Type == TURN || Type == FOCUS || Type == ZOOM; }
        }

        public override string ToString()
        {
            return "[ Type : " + Type + " , Value : " + Value + " ]";
        }

    }


    /// <summary>
    ///  <b>DIRECTION  CONSTANTS</b> <para />
    /// 
    /// 1-차원 : x축 > 1의 자리 <para />
    /// 2-차원 : y축 > 10의 자리 <para />
    /// 3-차원 : z축 > 100의 자리 <para />
    ///
    /// ( 0 = 해당 축에 값이 존재하지 않는 경우 ) <para />
    /// ( 유효한 수의 범위 = 1 ~ 9 )<para />
    /// ( 각 축의 영점 = 5 ) <para />
    /// 
    /// Example ) 2차원일 경우, (16방위표)       <para />
    ///               N                                   <para />                     
    ///               9-                                  <para />
    ///
    ///               7-    * => 77 .. NE 북동쪽.  <para />
    ///
    /// W 1-  3-  5-  7-  9- E (x축)     <para />
    ///
    ///               3-                                    <para />
    ///
    ///               1-    * => 17 .. SSE 남남동쪽     <para />
    ///               S                                           <para />           
    ///              (y축)                                     <para />      
    ///
    /// Example ) 3차원일 경우, 스마트폰 카메라 시야를 기준으로 축을 설정함. <para />
    ///
    /// </summary>
    public struct Direction
    {

        #region Constants

        public const int DIMENSION_NONE = 0;
        public const int DIMENSION_1 = 1;
        public const int DIMENSION_2 = 2;
        public const int DIMENSION_3 = 3;

        ///<summary>
        /// 0차원 : NO- DIRECTION
        ///</summary>
        public const int NONE = 0;  // 000

        /// <summary>
        ///  N 차원 : 중앙
        /// </summary>
        public const int CENTER = 5;

        /// <summary>
        ///  1차원 : 오른쪽
        /// </summary>
        public const int RIGHT = 7;  // 07

        /// <summary>
        ///  1차원 : 왼쪽
        /// </summary>
        public const int LEFT = 3;  // 03

        /// <summary>
        /// 1차원 : 위쪽
        /// </summary>
        public const int UP = 70;

        /// <summary>
        /// 1차원 : 아래쪽
        /// </summary>
        public const int DOWN = 30;

        /// <summary>
        /// 2차원 : 동쪽
        /// </summary>
        public const int EAST = 57;

        /// <summary>
        /// 2차원 : 서쪽
        /// </summary>
        public const int WEST = 53;

        /// <summary>
        /// 2차원 : 남쪽
        /// </summary>
        public const int SOUTH = 35;

        /// <summary>
        /// 2차원 : 북쪽
        /// </summary>
        public const int NORTH = 75;

        /// <summary>
        /// 3차원 : 앞쪽
        /// </summary>
        public const int FORWARD = 755;

        /// <summary>
        /// 3차원 : 뒷쪽
        /// </summary>
        public const int BACKWARD = 355;

        /// <summary>
        /// 3차원 : 하늘 방향
        /// </summary>
        public const int UPWARD = 575;

        /// <summary>
        /// 3차원 : 땅 방향
        /// </summary>
        public const int DOWNWARD = 535;

        /// <summary>
        /// 3차원 : 좌측 방향
        /// </summary>
        public const int LEFTWARD = 553;

        /// <summary>
        /// 3차원 : 우측 방향
        /// </summary>
        public const int RIGHTWARD = 557;

        /// <summary>
        /// Direction | Amount, 분리 계수
        /// </summary>
        public const int SEPARATION = 100000;
        #endregion Constants


        /// <summary>
        /// 방향
        /// </summary>
        public int Value;

        /// <summary>
        /// 이동/전환할 양
        /// </summary>
        public int Amount;

        public override string ToString()
        {
            return "[ Direction : " + GetDirectionName() + "(" + Value + ")" + " , Amount : " + Amount + " ]";
        }

        public string GetDirectionName()
        {
            switch (Value)
            {
                case NONE:
                    return "NONE";

                case CENTER:
                    return "CENTER";
                case RIGHT:
                    return "RIGHT";
                case LEFT:
                    return "LEFT";
                case UP:
                    return "UP";
                case DOWN:
                    return "DOWN";

                case EAST:
                    return "EAST";
                case WEST:
                    return "WEST";
                case SOUTH:
                    return "SOUTH";
                case NORTH:
                    return "NORTH";

                case FORWARD:
                    return "FORWARD";
                case BACKWARD:
                    return "BACKWARD";
                case UPWARD:
                    return "UPWARD";
                case DOWNWARD:
                    return "DOWNWARD";
                case LEFTWARD:
                    return "LEFTWARD";
                case RIGHTWARD:
                    return "RIGHTWARD";
                default:
                    return "";
            }

        }

    }

    /// <summary>
    /// Android 요청에 대한 결과
    /// </summary>
    public class RequestResult
    {
        ///
        /// (응답 반환시) 결과 상태 KEY.
        ///
        /// Ex)
        /// {
        ///     "result" : "success / fail / error"
        /// }
        ///
        public const string RESULT = "result";

        public const string STATUS_SUCCESS = "success";
        public const string STATUS_FAIL = "fail";
        public const string STATUS_ERROR = "error";

        public string RequestName;
        public string Status;

        public override string ToString()
        {
            return "QueryRequestResult : [\nRequest : " + RequestName
               + "\nStatus : " + Status
               + "\n]";
        }
    }

    /// <summary>
    /// Database Query 결과
    /// </summary>
    public class QueryRequestResult : RequestResult
    {
        public List<JsonData> QueryData;

        public override string ToString()
        {
            return "QueryRequestResult : [\nRequest : " + RequestName
                + "\nStatus : " + Status
                + "\nData : " + (QueryData == null ? "null" : QueryData.ToString())
                + "\n]";
        }
    }

    public class SpeechRequestResult : RequestResult
    {
        /// <summary>
        /// 음성인식 정보 key
        /// </summary>
        public const string SPEECH_REQUEST_KEY = "recognize_text_result";
        public string Speech;

        public override string ToString()
        {
            return "SpeechRequestResult  : [\nRequest : " + RequestName
                + "\nStatus : " + Status
                + "\nData : " + Speech
                + "\n]";
        }
    }
}

/// <summary>
/// VirtualPalace가 동작하는 형태
/// </summary>
public enum VirtualPalacePlayMode
{
    STANDARD,
    AR,
    VR
}
