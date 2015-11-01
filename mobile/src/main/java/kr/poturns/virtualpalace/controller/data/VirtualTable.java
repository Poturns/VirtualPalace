package kr.poturns.virtualpalace.controller.data;

/**
 * VIRTUAL TABLE 필드 상수
 */
public enum VirtualTable implements ITable{
    // Virtual Object ID (DB Index)
    _ID ("INTEGER PRIMARY KEY AUTOINCREMENT"),
    // Resource ID
    RES_ID ("INTEGER NOT NULL"),
    // Object Name (Unity Name)
    NAME ("TEXT"),
    // 오브젝트 타입
    TYPE ("INTEGER NOT NULL"),
    // 위치 좌표 값
    POS_X ("REAL NOT NULL"),
    POS_Y ("REAL NOT NULL"),
    POS_Z ("REAL NOT NULL"),
    // 회전 값
    ROTATE_X ("REAL"),
    ROTATE_Y ("REAL"),
    ROTATE_Z ("REAL"),
    // 크기 값
    SIZE_X ("REAL"),
    SIZE_Y ("REAL"),
    SIZE_Z ("REAL"),
    // 본 오브젝트를 포함하는 오브젝트 ID
    CONTAINER ("TEXT"),
    // Conatainer에 포함된 순서
    CONT_ORDER ("INTEGER"),
    // Customized Style
    STYLE ("TEXT");

    public String attributes;
    VirtualTable(String attr) {
        attributes = attr;
    }

    @Override
    public String getTableName() {
        return TABLE_VIRTUAL;
    }

    @Override
    public boolean equalString(String str) {
        return this.name().equalsIgnoreCase(str);
    }
}
