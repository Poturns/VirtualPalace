package kr.poturns.virtualpalace.controller;

public class GlobalSettings {

    // * * * S I N G L E T O N * * * //
    private static GlobalSettings sInstance;

    public static GlobalSettings getInstance() {
        if (sInstance == null)
            sInstance = new GlobalSettings();
        return sInstance;
    }

    private GlobalSettings() {

    }

}
