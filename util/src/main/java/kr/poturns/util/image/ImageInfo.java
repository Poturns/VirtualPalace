package kr.poturns.util.image;


import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ImageInfo implements Parcelable {
    public static final String[] PROJECTION = {
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME
    };
    public int id;
    public String imagePath;
    public String displayName, bucketName;



    public static String getJsonImageInfoList(Context context, String bucketName) {
        List<ImageInfo> list = getImageInfoList(context, bucketName);

        final int N = list.size();
        JSONArray jsonArray = new JSONArray();

        for (int i = 0; i < N; i++) {
            jsonArray.put(list.get(i).toJson());
        }

        return jsonArray.toString();
    }

    public JSONObject toJson(){
        try {
            return new JSONObject().put("id", id).put("imagePath", imagePath).put("displayName", displayName).put("bucketName", bucketName);
        } catch (Exception e) {
            return new JSONObject();
        }
    }

    static List<ImageInfo> getImageInfoList(Context context, String bucketName) {
        String[] selectionArgs = {bucketName};
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                ImageInfo.PROJECTION,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME + "=?",
                selectionArgs,
                MediaStore.Images.Media.DISPLAY_NAME
        );

        ArrayList<ImageInfo> imageInfoArrayList = new ArrayList<ImageInfo>();

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    ImageInfo imageInfo = ImageInfo.getImageInfoFromCursor(cursor, bucketName);
                    if (imageInfo != null) {
                        imageInfoArrayList.add(imageInfo);
                    }
                }
                while (cursor.moveToNext());

                cursor.close();
            }
        }

        return imageInfoArrayList;
    }

    static ImageInfo getImageInfoFromCursor(Cursor cursor, String bucketName) {

        int idIndex = cursor.getColumnIndex(PROJECTION[0]);
        int pathIdx = cursor.getColumnIndex(PROJECTION[1]);
        int nameIdx = cursor.getColumnIndex(PROJECTION[2]);

        int imageID = cursor.getInt(idIndex);
        String imagePath = cursor.getString(pathIdx);
        String displayName = cursor.getString(nameIdx);
        if (displayName != null) {
            ImageInfo imageInfo = new ImageInfo();
            imageInfo.id = imageID;
            imageInfo.imagePath = imagePath;
            imageInfo.displayName = displayName;
            imageInfo.bucketName = bucketName;
            return imageInfo;
        }

        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.imagePath);
        dest.writeString(this.displayName);
        dest.writeString(this.bucketName);
    }

    public ImageInfo() {
    }

    protected ImageInfo(Parcel in) {
        this.id = in.readInt();
        this.imagePath = in.readString();
        this.displayName = in.readString();
        this.bucketName = in.readString();
    }

    public static final Creator<ImageInfo> CREATOR = new Creator<ImageInfo>() {
        public ImageInfo createFromParcel(Parcel source) {
            return new ImageInfo(source);
        }

        public ImageInfo[] newArray(int size) {
            return new ImageInfo[size];
        }
    };


    public Bitmap loadSuitableBitmap() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);

        int size = options.outHeight * options.outWidth;
        if (size > 100000) {
            options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            if (size > 1000000) {
                options.inSampleSize = 8;
            }

            return BitmapFactory.decodeFile(imagePath, options);
        } else {
            return BitmapFactory.decodeFile(imagePath);
        }
    }
}