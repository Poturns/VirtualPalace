package kr.poturns.virtualpalace.controller.data;

/**
 * RESOURCE TABLE 필드 상수
 */
public enum ResourceTable implements ITable {
    _ID ("INTEGER PRIMARY KEY AUTOINCREMENT"),
    TITLE("TEXT"),
    CONTENTS("TEXT"),
    // TEXT = 0, IMAGE = 1, MOVIE = 2
    RES_TYPE("INTEGER"),
    EXPANSION("TEXT"),
    //ARCHIVE_PATH ("TEXT"),
    //DRIVE_PATH ("TEXT"),
    //DRIVE_KEY ("TEXT"),
    //DESCRIPTION ("TEXT"),
    //THUMBNAIL_PATH ("TEXT"),
    CTIME ("INTEGER"),
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
    public boolean isTextField() {
        return attributes.contains("TEXT");
    }

    @Override
    public boolean equalString(String str) {
        return this.name().equalsIgnoreCase(str);
    }

}
