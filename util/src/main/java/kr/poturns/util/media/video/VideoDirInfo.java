package kr.poturns.util.media.video;


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import kr.poturns.util.media.BaseDirInfo;

public class VideoDirInfo extends BaseDirInfo<VideoInfo> {
    public static final String[] PROJECTION = {MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME};

    public List<VideoInfo> getInfoList(Context context) {
        return VideoInfo.getInfoList(context, dirName);
    }

    public static String getJSONDirList(Context context) {
        List<VideoDirInfo> list = getDirList(context);

        final int N = list.size();
        JSONArray jsonArray = new JSONArray();

        for (int i = 0; i < N; i++) {
            jsonArray.put(list.get(i).toJSON());
        }

        return jsonArray.toString();
    }

    public static List<VideoDirInfo> getDirList(Context context) {
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                VideoDirInfo.PROJECTION,
                MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME + " IS NOT NULL )" +
                        " GROUP BY (" + MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME,
                null,
                MediaStore.Video.Media.DEFAULT_SORT_ORDER
        );

        List<VideoDirInfo> infoList = new ArrayList<VideoDirInfo>();

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    int dirNameIdx = cursor.getColumnIndex(VideoDirInfo.PROJECTION[0]);

                    do {
                        String dirName = cursor.getString(dirNameIdx);
                        if (dirName != null) {

                            VideoDirInfo info = new VideoDirInfo();
                            info.dirName = dirName;
                            info.firstInfo = getDirFirstItem(resolver, dirName);
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

    private static VideoInfo getDirFirstItem(ContentResolver resolver, String bucketName) {
        String[] selectionArgs = {bucketName};
        Cursor cursor = resolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                VideoInfo.PROJECTION,
                MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME + "=?",
                selectionArgs,
                MediaStore.Video.VideoColumns.DISPLAY_NAME + " ASC  LIMIT 1"
        );

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    return VideoInfo.fromCursor(cursor, bucketName);
                }
            } finally {
                cursor.close();
            }
        }

        return null;
    }
}
