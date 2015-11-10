using System.Collections.Generic;

namespace BridgeApi.Media
{
    public interface BaseDirInfo<T> where T : BaseInfo
    {
        /// <summary>
        /// ���丮 �̸�
        /// </summary>
        string DirName { get; set; }

        /// <summary>
        /// ���丮�� �����ϴ� Info�� ù��° Info
        /// </summary>
        T FirstInfo { get; set; }

        /// <summary>
        /// ���丮�� �����ϴ� ��� Info�� ����Ʈ�� ��ȯ�Ѵ�.
        /// </summary>
        /// <returns>���丮�� �����ϴ� ��� Info�� ����Ʈ</returns>
        List<T> GetInfoListInDirectory();

    }
}

