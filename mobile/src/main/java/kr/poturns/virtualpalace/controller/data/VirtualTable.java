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
    NAME("TEXT"),
    // 본 오브젝트를 포함하는 오브젝트 ID
    PARENT_NAME("TEXT"),
    // 오브젝트 타입
    MODEL_TYPE ("INTEGER NOT NULL"),
    // 위치 좌표 값
    POS_X ("REAL"),
    POS_Y ("REAL"),
    POS_Z ("REAL"),
    // 회전 값
    ROTATE_X ("REAL"),
    ROTATE_Y ("REAL"),
    ROTATE_Z ("REAL"),
    ROTATE_W ("REAL"),
    // 크기 값
    SIZE_X ("REAL"),
    SIZE_Y ("REAL"),
    SIZE_Z ("REAL");

    public String attributes;
    VirtualTable(String attr) {
        attributes = attr;
    }

    @Override
    public String getTableName() {
        return TABLE_VIRTUAL;
    }

    @Override
    public boolean isTextField() {
        return attributes.contains("TEXT");
    }

    @Override
    public boolean equalString(String str) {
        return this.name().equalsIgnoreCase(str);
    }
}
