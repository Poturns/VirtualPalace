package kr.poturns.virtualpalace.controller.data;


public enum VRContainerTable implements ITable{
    _ID ("INTEGER PRIMARY KEY AUTOINCREMENT"),
    NAME("TEXT"),
    Z_OFFSET ("REAL DEFAULT 0"),
    COUNT ("INTEGER DEFAULT 0");

    public String attributes;
    VRContainerTable(String attr) {
        attributes = attr;
    }

    @Override
    public String getTableName() {
        return TABLE_VR_CONTAINER;
    }

    @Override
    public boolean equalString(String str) {
        return this.name().equalsIgnoreCase(str);
    }
}
