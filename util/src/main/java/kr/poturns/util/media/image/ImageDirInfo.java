package kr.poturns.util.media.image;


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import kr.poturns.util.media.BaseDirInfo;

public class ImageDirInfo extends BaseDirInfo<ImageInfo> {
    public static final String[] PROJECTION = {MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME};

    public List<ImageInfo> getInfoList(Context context) {
        return ImageInfo.getInfoList(context, dirName);
    }

    public static String getJSONDirList(Context context) {
        List<ImageDirInfo> list = getDirList(context);

        final int N = list.size();
        JSONArray jsonArray = new JSONArray();

        for (int i = 0; i < N; i++) {
            jsonArray.put(list.get(i).toJSON());
        }

        return jsonArray.toString();
    }

    public static List<ImageDirInfo> getDirList(Context context) {
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                ImageDirInfo.PROJECTION,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME + " IS NOT NULL )" +
                        " GROUP BY (" + MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                null,
                MediaStore.Images.Media.DEFAULT_SORT_ORDER
        );

        List<ImageDirInfo> infoList = new ArrayList<ImageDirInfo>();

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    int bucketNameIdx = cursor.getColumnIndex(ImageDirInfo.PROJECTION[0]);

                    do {
                        String bucketName = cursor.getString(bucketNameIdx);
                        if (bucketName != null) {

                            ImageDirInfo info = new ImageDirInfo();
                            info.dirName = bucketName;
                            info.firstInfo = getDirFirstItem(resolver, bucketName);
                            infoList.add(info);
                        }
                    }
                    while (cursor.moveToNext());
                }
            } finally {
                cursor.close();
            }
        }

        return infoList;
    }

    private static ImageInfo getDirFirstItem(ContentResolver resolver, String bucketName) {
        String[] selectionArgs = {bucketName};
        Cursor cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                ImageInfo.PROJECTION,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME + "=?",
                selectionArgs,
                MediaStore.Images.Media.DISPLAY_NAME + " ASC  LIMIT 1"
        );

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    return ImageInfo.fromCursor(cursor, bucketName);
                }
            } finally {
                cursor.close();
            }
        }

        return null;
    }
}