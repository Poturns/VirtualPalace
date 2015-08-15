package kr.poturns.util.media;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

public abstract class BaseInfo extends Info implements Parcelable {
    public int id;
    public String path;
    public String displayName;

    public BaseInfo() {
    }

    protected BaseInfo(Parcel in) {
        this.id = in.readInt();
        this.path = in.readString();
        this.displayName = in.readString();
        this.dirName = in.readString();
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
