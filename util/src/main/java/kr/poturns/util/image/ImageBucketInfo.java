package kr.poturns.util.image;


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ImageBucketInfo {
    public static final String[] PROJECTION = {MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME};
    public final String bucketName;
    public ImageInfo firstImageInfo;

    public ImageBucketInfo(String bucketName) {
        this.bucketName = bucketName;
    }

    public List<ImageInfo> getImageInfoList(Context context) {
        return ImageInfo.getImageInfoList(context, bucketName);
    }

    public JSONObject toJson() {
        try {
            return new JSONObject().put("bucketName", bucketName).put("firstImageInfo", firstImageInfo.toJson());
        } catch (Exception e) {
            return new JSONObject();
        }
    }

    public static String getJsonImageBucketList(Context context) {
        List<ImageBucketInfo> list = getImageBucketList(context);

        final int N = list.size();
        JSONArray jsonArray = new JSONArray();

        for (int i = 0; i < N; i++) {
            jsonArray.put(list.get(i).toJson());
        }

        return jsonArray.toString();
    }

    public static List<ImageBucketInfo> getImageBucketList(Context context) {
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                ImageBucketInfo.PROJECTION,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME + " IS NOT NULL )" +
                        " GROUP BY (" + MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                null,
                MediaStore.Images.Media.DEFAULT_SORT_ORDER
        );

        List<ImageBucketInfo> infoList = new ArrayList<ImageBucketInfo>();

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int bucketNameIdx = cursor.getColumnIndex(ImageBucketInfo.PROJECTION[0]);

                do {
                    String bucketName = cursor.getString(bucketNameIdx);
                    if (bucketName != null) {

                        ImageBucketInfo info = new ImageBucketInfo(bucketName);
                        info.firstImageInfo = getImageBucketFirstItem(resolver, bucketName);
                        infoList.add(info);
                    }
                }
                while (cursor.moveToNext());

                cursor.close();
            }
        }

        return infoList;
    }

    private static ImageInfo getImageBucketFirstItem(ContentResolver resolver, String bucketName) {
        String[] selectionArgs = {bucketName};
        Cursor cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                ImageInfo.PROJECTION,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME + "=?",
                selectionArgs,
                MediaStore.Images.Media.DISPLAY_NAME + " ASC  LIMIT 1"
        );


        if (cursor != null) {
            if (cursor.moveToFirst()) {
                return ImageInfo.getImageInfoFromCursor(cursor, bucketName);
            }
            cursor.close();

        }

        return null;
    }
}