package kr.poturns.virtualpalace.controller.data;

/**
 * AUGMENTED TABLE 필드 상수
 */
public enum AugmentedTable implements ITable {
    // Augmented Reality ID
    _ID ("INTEGER PRIMARY KEY AUTOINCREMENT"),
    // Resource ID
    RES_ID ("INTEGER NOT NULL"),
    // 고도
    ALTITUDE ("REAL"),
    // 위도
    LATITUDE ("REAL"),
    // 경도
    LONGITUDE ("REAL"),
    // 입체 보완 좌표 X
    SUPPORT_X ("REAL"),
    // 입체 보완 좌표 Y
    SUPPORT_Y ("REAL"),
    // 입체 보완 좌표 Z
    SUPPORT_Z ("REAL");

    public String attributes;
    AugmentedTable(String attr) {
        attributes = attr;
    }

    @Override
    public String getTableName() {
        return TABLE_AUGMENTED;
    }

    @Override
    public boolean equalString(String str) {
        return this.name().equalsIgnoreCase(str);
    }
}
