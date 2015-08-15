package kr.poturns.util.media.image;


import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.provider.MediaStore;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import kr.poturns.util.media.BaseInfo;

public class ImageInfo extends BaseInfo {
    public static final String[] PROJECTION = {
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME
    };

    public ImageInfo() {
        super();
    }

    public static String getJSONInfoList(Context context, String dirName) {
        List<ImageInfo> list = getInfoList(context, dirName);

        final int N = list.size();
        JSONArray jsonArray = new JSONArray();

        for (int i = 0; i < N; i++) {
            jsonArray.put(list.get(i).toJSON());
        }

        return jsonArray.toString();
    }

    static List<ImageInfo> getInfoList(Context context, String dirName) {
        String[] selectionArgs = {dirName};
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
                    ImageInfo imageInfo = ImageInfo.fromCursor(cursor, dirName);
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

    static ImageInfo fromCursor(Cursor cursor, String bucketName) {

        int idIndex = cursor.getColumnIndex(PROJECTION[0]);
        int pathIdx = cursor.getColumnIndex(PROJECTION[1]);
        int nameIdx = cursor.getColumnIndex(PROJECTION[2]);

        int imageID = cursor.getInt(idIndex);
        String imagePath = cursor.getString(pathIdx);
        String displayName = cursor.getString(nameIdx);
        if (displayName != null) {
            ImageInfo imageInfo = new ImageInfo();
            imageInfo.id = imageID;
            imageInfo.path = imagePath;
            imageInfo.displayName = displayName;
            imageInfo.dirName = bucketName;
            return imageInfo;
        }

        return null;
    }

    protected ImageInfo(Parcel in) {
        super(in);
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
        BitmapFactory.decodeFile(path, options);

        int size = options.outHeight * options.outWidth;
        if (size > 100000) {
            options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            if (size > 1000000) {
                options.inSampleSize = 8;
            } else if (size > 500000) {
                options.inSampleSize = 4;
            }

            return BitmapFactory.decodeFile(path, options);
        } else {
            return BitmapFactory.decodeFile(path);
        }
    }
}