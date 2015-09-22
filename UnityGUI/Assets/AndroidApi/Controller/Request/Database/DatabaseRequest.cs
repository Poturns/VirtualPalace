using LitJson;

namespace AndroidApi.Controller.Request.Database
{
    /*
      Command : {
                Operation : {       
                 Field - Value

                }
            }

        예시 : 
      INSERT_VR/AR/RES : {
                SET : {
                 //  삽입할 필드 - 값

                }
            }
    */
    /// <summary>
    /// Database관련 처리를 요청하는 IRequest
    /// </summary>
    public class DatabaseRequest : IRequest
    {
        internal JsonData jData = new JsonData();
   
        public IInsert INSERT
        {
            get;
            
        }

        public IUpdate UPDATE
        {
            get;
            
        }

        public IDelete DELETE
        {
            get;
           
        }

        public DatabaseRequest()
        {
            INSERT = new InsertCommand(this);
            UPDATE = new UpdateCommand(this);
            DELETE = new DeleteCommand(this);
        }

        /// <summary>
        /// 작성한 모든 요청을 초기화 한다.
        /// </summary>
        /// <returns>DatabaseRequest</returns>
        public DatabaseRequest Clear()
        {
            jData.Clear();
            return this;
        }

        public string ToJson()
        {
            return jData.ToJson();
        }

    }


}
