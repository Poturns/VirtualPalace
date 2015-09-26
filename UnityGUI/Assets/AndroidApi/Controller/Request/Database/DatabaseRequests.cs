using LitJson;
using System;
using System.Collections;

namespace AndroidApi.Controller.Request.Database
{

    #region Database Property

    /// <summary>
    /// Database에서 조작할 Table
    /// </summary>
    public enum Place
    {
        VR, AR, RES
    }

    /// <summary>
    /// RESOURCE TABLE 필드 상수
    /// </summary>
    public enum RESOURCE_FIELD
    {
        _ID,
        NAME,
        TYPE,
        CATEGORY,
        ARCHIVE_PATH,
        ARCHIVE_KEY,
        DRIVE_PATH,
        DRIVE_KEY,
        THUMBNAIL_PATH,
        DESCRIPTION,
        CTIME,
        MTIME

    }

    /// <summary>
    /// VIRTUAL TABLE 필드 상수
    /// </summary>
    public enum VIRTUAL_FIELD
    {
        _ID,
        RESID,
        TYPE,
        POS_X,
        POS_Y,
        POS_Z,
        ROTATE_X,
        ROTATE_Y,
        ROTATE_Z,
        CONTAINER,
        CONT_ORDER,
        STYLE

    }

    /// <summary>
    /// AUGMENTED TABLE 필드 상수
    /// </summary>
    public enum AUGMENTED_FIELD
    {
        _ID,
        RESID,
        ALTITUDE,
        LATITUDE,
        LONGITUDE

    }

    #endregion Database Property


    #region Impl

    /*
    형식
    Command : {
                Operation : {       
                    Field - Value

                }
            }

    실제
    INSERT_VR/AR/RES : {
                SET : {
                    //  삽입할 필드 - 값

                }
            }

    UPDATE_VR/AR/RES : {
            SET : {
                //  수정할 필드 - 값

            },
            WHERE : {
                // 수정할 데이터의 조건 : EQUAL : 필드 - 값

            }
            WHERE_NOT : {
                // 수정할 데이터의 조건 : NOT_EQUAL : 필드 - 값

            }
            WHERE_GREATER : {
                // GREATER : 필드 - 값

                ALLOW_EQUAL : true / false
            }
            WHERE_SMALLER : {
                // SMALLER : 필드 - 값

                ALLOW_EQUAL : true / false
            }
            WHERE_FROM : {
                // 수정할 데이터의 조건 : BETWEEN, FROM : 필드 - 값
                // 반드시 WHERE_TO 와 함께 기술되어야 함.

                }
                WHERE_TO : {
                // 수정할 데이터의 조건 : BETWEEN, TO : 필드 - 값
                // 반드시 WHERE_FROM 과 함께 기술되어야 함.

                }
                WHERE_LIKE : {
                // 수정할 데이터의 조건 : LIKE : 필드 - 값

                }
        }

        DELETE_VR/AR/RES : {
            // 삭제할 데이터의 조건 : 필드 - 값
            WHERE : {
                // EQUAL : 필드 - 값

            }
            WHERE_NOT : {
                // NOT_EQUAL : 필드 - 값

            }
            WHERE_GREATER : {
                // GREATER : 필드 - 값

                ALLOW_EQUAL : true / false
            }
            WHERE_SMALLER : {
                // SMALLER : 필드 - 값

                ALLOW_EQUAL : true / false
            }
            WHERE_FROM : {
                // BETWEEN, FROM : 필드 - 값
                // 반드시 WHERE_TO 와 함께 기술되어야 함.

                }
                WHERE_TO : {
                // BETWEEN, TO : 필드 - 값
                // 반드시 WHERE_FROM 과 함께 기술되어야 함.

                }
                WHERE_LIKE : {
                // LIKE : 필드 - 값

                }

        }
    */
    internal abstract class AbtractCommand : ICommand
    {
        #region Constants
        protected const string SUFFIX_AR = "_ar";
        protected const string SUFFIX_VR = "_vr";
        protected const string SUFFIX_RES = "_res";

        protected const string COMMAND_INSERT = "insert";
        protected const string COMMAND_UPDATE = "update";
        protected const string COMMAND_DELETE = "delete";

        protected const string ALLOW_EQUAL = "allow_equal";

        protected const string OPERATION_SET = "set";
        protected const string OPERATION_WHERE = "where";
        protected const string OPERATION_WHERE_NOT = "where_not";
        protected const string OPERATION_WHERE_GREATER = "where_greater";
        protected const string OPERATION_WHERE_SMALLER = "where_smaller";
        protected const string OPERATION_WHERE_FROM = "where_from";
        protected const string OPERATION_WHERE_TO = "where_to";
        protected const string OPERATION_WHERE_LIKE = "where_like";

        protected const string SWITCH_PLAY_MODE = "switch_play_mode";
        protected const string ACTIVATE_INPUT = "activate_input";
        protected const string DEACTIVATE_INPUT = "deactivate_input";

        #endregion Constants


        protected internal DatabaseRequest Request;
        protected string Operation;


        protected internal AbtractCommand(DatabaseRequest request, string operation)
        {
            Request = request;
            Operation = operation;
        }

        public IDatabaseRequest End()
        {
            return Request;
        }

        internal void AddCommand(Place place, string command, Enum fieldEnum, string value)
        {
            string operation = Operation + place.ToString();
            command = operation;
            string field = fieldEnum.ToString().ToLowerInvariant();

            JsonData operationJson = JsonUtil.InsureChild(Request.jData, operation);
            JsonData commandJson = JsonUtil.InsureChild(operationJson, command);

            commandJson[field] = value;
        }

        internal void AddCommandAndAllowEqual(Place place, string command, Enum fieldEnum, string value, bool allowEqual)
        {
            AddCommand(place, command, fieldEnum, value);

            string operation = (Operation + place.ToString()).ToLowerInvariant();
            Request.jData[operation][command][ALLOW_EQUAL] = allowEqual;
        }

    }

    internal class InsertCommand : AbtractCommand, IInsert
    {
        internal InsertCommand(DatabaseRequest request) : base(request, COMMAND_INSERT)
        {
        }

        public IInsert Set(Place place, Enum field, string value)
        {
            AddCommand(place, OPERATION_SET, field, value);
            return this;
        }

    }

    internal class UpdateCommand : AbtractCommand, IUpdate, IWhereFrom<IUpdate>
    {
        internal UpdateCommand(DatabaseRequest request) : base(request, COMMAND_UPDATE)
        {
        }

        public IUpdate Set(Place place, Enum field, string value)
        {
            AddCommand(place, OPERATION_SET, field, value);
            return this;
        }

        public IUpdate Where(Place place, Enum field, string value)
        {
            AddCommand(place, OPERATION_WHERE, field, value);
            return this;
        }

        public IUpdate WhereGreater(Place place, Enum field, string value, bool allowEqual)
        {
            AddCommandAndAllowEqual(place, OPERATION_WHERE_GREATER, field, value, allowEqual);
            return this;
        }

        public IUpdate WhereSmaller(Place place, Enum field, string value, bool allowEqual)
        {
            AddCommandAndAllowEqual(place, OPERATION_WHERE_SMALLER, field, value, allowEqual);
            return this;
        }

        public IWhereFrom<IUpdate> WhereFrom(Place place, Enum field, string value)
        {
            AddCommand(place, OPERATION_WHERE_FROM, field, value);
            return this;
        }

        public IUpdate WhereTo(Place place, Enum field, string value)
        {
            AddCommand(place, OPERATION_WHERE_TO, field, value);
            return this;
        }

        public IUpdate WhereLike(Place place, Enum field, string value)
        {
            AddCommand(place, OPERATION_WHERE_LIKE, field, value);
            return this;
        }


    }

    internal class DeleteCommand : AbtractCommand, IDelete, IWhereFrom<IDelete>
    {
        internal DeleteCommand(DatabaseRequest request) : base(request, COMMAND_DELETE)
        {
        }

        public IDelete Where(Place place, Enum field, string value)
        {
            AddCommand(place, OPERATION_WHERE, field, value);
            return this;
        }

        public IDelete WhereGreater(Place place, Enum field, string value, bool allowEqual)
        {
            AddCommandAndAllowEqual(place, OPERATION_WHERE_GREATER, field, value, allowEqual);
            return this;
        }

        public IDelete WhereSmaller(Place place, Enum field, string value, bool allowEqual)
        {
            AddCommandAndAllowEqual(place, OPERATION_WHERE_SMALLER, field, value, allowEqual);
            return this;
        }

        public IWhereFrom<IDelete> WhereFrom(Place place, Enum field, string value)
        {
            AddCommand(place, OPERATION_WHERE_FROM, field, value);
            return this;
        }

        public IDelete WhereTo(Place place, Enum field, string value)
        {
            AddCommand(place, OPERATION_WHERE_TO, field, value);
            return this;
        }

        public IDelete WhereLike(Place place, Enum field, string value)
        {
            AddCommand(place, OPERATION_WHERE_LIKE, field, value);
            return this;
        }
    }

    #endregion Impl


    internal class JsonUtil
    {

        /// <summary>
        /// 주어진 Json 내부에 해당 키값을 가지는 Json을 반환한다.
        /// 
        /// * 기존의 데이터는 지워진다.
        /// </summary>
        /// <param name="json">확인할 Json</param>
        /// <param name="key">json 내부에 존재하는 json의 key</param>
        /// <returns>json 내부에 존재하는 json</returns>
        public static JsonData InsureChild(JsonData json, string key)
        {
            JsonData childJson;
            if (JsonDataContainsKey(json, key))
                childJson = json[key];
            else
            {
                childJson = new JsonData();
                json[key] = childJson;
            }

            return childJson;
        }

        /// <summary>
        /// Json 내부에 해당 key값이 존재하는지 확인한다.
        /// </summary>
        /// <param name="json">검사할 json</param>
        /// <param name="key">확인할 key</param>
        /// <returns>key가 존재하는지 여부</returns>
        public static bool JsonDataContainsKey(JsonData json, string key)
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
    }
}
