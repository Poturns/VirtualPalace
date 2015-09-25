using LitJson;
using System;

namespace AndroidApi.Controller.Request.Database
{
    /// <summary>
    /// Database관련 처리를 요청하는 IRequest
    /// </summary>
    public interface IDatabaseRequest : IRequest
    {

        IInsert INSERT
        {
            get;
        }

        IUpdate UPDATE
        {
            get;
        }

        IDelete DELETE
        {
            get;
        }

    }


    public interface ICommand
    {
        /// <summary>
        /// Database Operation을 마치고 추가적으로 다른 Operation을 요청하기 위해 IDatabaseRequest객체를 반환한다.
        /// <para />
        /// * 추가적으로 Operation을 요청하지 않는다면 이 메소드를 호출하지 않아도 상관없다.
        /// </summary>
        /// <returns>IDatabaseRequest 객체</returns>
        IDatabaseRequest End();
    }

    public interface IInsert : ICommand
    {
        IInsert Set(Place place, Enum field, string value);
    }

    public interface IDelete : ICommand
    {
        IDelete Where(Place place, Enum field, string value);
        IDelete WhereGreater(Place place, Enum field, string value, bool allowEqual);
        IDelete WhereSmaller(Place place, Enum field, string value, bool allowEqual);
        IWhereFrom<IDelete> WhereFrom(Place place, Enum field, string value);
        IDelete WhereLike(Place place, Enum field, string value);
    }

    public interface IUpdate : ICommand
    {
        IUpdate Set(Place place, Enum field, string value);
        IUpdate Where(Place place, Enum field, string value);
        IUpdate WhereGreater(Place place, Enum field, string value, bool allowEqual);
        IUpdate WhereSmaller(Place place, Enum field, string value, bool allowEqual);
        IWhereFrom<IUpdate> WhereFrom(Place place, Enum field, string value);
        IUpdate WhereLike(Place place, Enum field, string value);
    }

    public interface IWhereFrom<T>
    {
        T WhereTo(Place place, Enum field, string value);
    }


    public static class DatabaseRequests
    {
        public static IDatabaseRequest NewRequest()
        {
            return new DatabaseRequest();
        }

    }

    internal class DatabaseRequest : IDatabaseRequest
    {
        internal JsonData jData = new JsonData();

        public IInsert INSERT
        {
            get; private set;
        }

        public IUpdate UPDATE
        {
            get; private set;
        }

        public IDelete DELETE
        {
            get; private set;
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
