
namespace MyScript.Interface
{
    /// <summary>
    /// Scene을 변경시킬 수 있는 IStateBase
    /// </summary>
    public interface ISceneChangeState : IStateBase
    {
        /// <summary>
        /// Scene이 변경 완료되었을 때 호출된다.
        /// </summary>
        void OnSceneChanged();
    }
}
