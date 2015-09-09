package kr.poturns.virtualpalace.media;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

/**
 * 기본적인 정보가 구현된 {@link MediaInfo}
 */
public abstract class BaseMediaInfo extends MediaInfo implements Parcelable {
    /**
     * 미디어의 ID
     */
    public int id;
    /**
     * 미디어가 저장된 경로
     */
    public String path;
    /**
     * 미디어가 기타 앱에서 표시되는 이름
     * <p>
     * <b>대부분 파일이름과 동일하지만 반드시 그런것은 아님.</b>
     */
    public String displayName;

    public BaseMediaInfo() {
    }

    @Override
    public JSONObject toJSON() {
        try {
            return new JSONObject()
                    .put("id", id)
                    .put("path", path)
                    .put("displayName", displayName)
                    .put("dirName", dirName);

        } catch (Exception e) {
            return new JSONObject();
        }

    }

    //***** Parcelable Interface *****//

    protected BaseMediaInfo(Parcel in) {
        this.id = in.readInt();
        this.path = in.readString();
        this.displayName = in.readString();
        this.dirName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.path);
        dest.writeString(this.displayName);
        dest.writeString(this.dirName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

}
