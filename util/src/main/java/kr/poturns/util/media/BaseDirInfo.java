package kr.poturns.util.media;


import org.json.JSONObject;

public abstract class BaseDirInfo<T extends BaseInfo> extends Info {
    public T firstInfo;

    @Override
    public JSONObject toJSON() {
        try {
            return new JSONObject()
                    .put("dirName", dirName)
                    .put("firstInfo", firstInfo.toJSON());

        } catch (Exception e) {
            return new JSONObject();
        }

    }

}
