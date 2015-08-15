package kr.poturns.util.media;


import org.json.JSONObject;

public abstract class Info {
    public String dirName;

    public abstract JSONObject toJSON();
}
