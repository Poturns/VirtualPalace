package kr.poturns.virtualpalace.controller.data;

/**
 * 테이블 필드 상수를 묶기 위한 인터페이스.
 */
public interface ITable {
    /**
     * RESOURE TABLE 명
     */
    String TABLE_RESOURCE = "resource";
    /**
     * VIRTUAL TABLE 명
     */
    String TABLE_VIRTUAL = "virtual";
    /**
     * AUGMENTED TABLE 명
     */
    String TABLE_AUGMENTED = "augmented";

    /**
     * 해당 필드가 소속되어 있는 테이블 명을 반환한다.
     * @return
     */
    String getTableName();

    /**
     * toString() + equalsIgnoreCase()
     * @param str
     * @return
     */
    boolean equalString(String str);

    /**
     * 해당 필드의 DB 내 순서 값을 반환한다.
     * @return
     */
    // enum 키워드 내에 이미 구현되어 있으나, 인터페이스로 호출하기 위해 선언.
    int ordinal();

}
