
namespace MyScript.Interface
{
	public enum OBJECT_KIND
	{
		BOOK,
		DECO
	};
	public interface IRaycastedObject
	{
		void OnSelect();
	}
}