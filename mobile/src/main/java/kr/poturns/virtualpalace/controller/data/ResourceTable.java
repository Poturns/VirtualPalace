package kr.poturns.virtualpalace.controller.data;

/**
 * RESOURCE TABLE 필드 상수
 */
public enum ResourceTable implements ITable {
    _ID ("INTEGER PRIMARY KEY AUTOINCREMENT"),
    NAME ("TEXT NOT NULL"),
    TYPE ("TEXT NOT NULL"),
    CATEGORY ("TEXT"),
    ARCHIVE_PATH ("TEXT"),
    ARCHIVE_KEY ("TEXT"),
    DRIVE_PATH ("TEXT"),
    DRIVE_KEY ("TEXT"),
    THUMBNAIL_PATH ("TEXT"),
    DESCRIPTION ("TEXT"),
    CTIME ("INTEGER NOT NULL"),
    MTIME ("INTEGER");

    public String attributes;
    ResourceTable(String attr) {
        attributes = attr;
    }

    @Override
    public String getTableName() {
        return TABLE_RESOURCE;
    }

    @Override
    public boolean equalString(String str) {
        return this.name().equalsIgnoreCase(str);
    }

}
