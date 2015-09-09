package kr.poturns.virtualpalace.media.video;


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import kr.poturns.virtualpalace.annotation.UnityApi;
import kr.poturns.virtualpalace.media.BaseMediaDirInfo;

/**
 * 비디오가 저장된 폴더의 메타데이터
 */
@UnityApi
public class VideoDirInfo extends BaseMediaDirInfo<VideoInfo> {
    public static final String[] PROJECTION = {MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME};

    @Override
    public List<VideoInfo> getInfoList(Context context) {
        return VideoInfo.getInfoList(context, dirName);
    }

    /**
     * 비디오가 저장된 모든 폴더의 메타데이터를 얻는다.
     *
     * @param context 리스트를 쿼리할 때 사용하는 context
     * @return 비디오가 저장된 폴더의 메타데이터의 리스트를 JSON 형식으로 표현한 문자열
     */
    @UnityApi
    public static String getJSONDirList(Context context) {
        List<VideoDirInfo> list = getDirList(context);

        final int N = list.size();
        JSONArray jsonArray = new JSONArray();

        for (int i = 0; i < N; i++) {
            jsonArray.put(list.get(i).toJSON());
        }

        return jsonArray.toString();
    }

    /**
     * 비디오가 저장된 모든 폴더의 메타데이터를 얻는다.
     *
     * @param context 리스트를 쿼리할 때 사용하는 context
     * @return 비디오가 저장된 모든 폴더의 메타데이터의 리스트
     */
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

    /**
     * 주어진 이름을 가진 폴더에서 첫 비디오 메타데이터를 반환한다.
     *
     * @param resolver 리스트를 쿼리할 때 사용하는 ContentResolver
     * @return 해당 폴더에서 맨 처음으로 나타나는 비디오 메타데이터
     */
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
