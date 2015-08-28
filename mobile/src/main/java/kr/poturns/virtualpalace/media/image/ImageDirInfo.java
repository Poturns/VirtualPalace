package kr.poturns.virtualpalace.media.image;


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
 * 이미지가 저장된 폴더의 메타데이터
 */
@UnityApi
public class ImageDirInfo extends BaseMediaDirInfo<ImageInfo> {
    public static final String[] PROJECTION = {MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME};

    @Override
    public List<ImageInfo> getInfoList(Context context) {
        return ImageInfo.getInfoList(context, dirName);
    }

    /**
     * 이미지가 저장된 모든 폴더의 메타데이터를 얻는다.
     *
     * @param context 리스트를 쿼리할 때 사용하는 context
     * @return 이미지가 저장된 폴더의 메타데이터의 리스트를 JSON 형식으로 표현한 문자열
     */
    @UnityApi
    public static String getJSONDirList(Context context) {
        List<ImageDirInfo> list = getDirList(context);

        final int N = list.size();
        JSONArray jsonArray = new JSONArray();

        for (int i = 0; i < N; i++) {
            jsonArray.put(list.get(i).toJSON());
        }

        return jsonArray.toString();
    }

    /**
     * 이미지가 저장된 모든 폴더의 메타데이터를 얻는다.
     *
     * @param context 리스트를 쿼리할 때 사용하는 context
     * @return 이미지가 저장된 모든 폴더의 메타데이터의 리스트
     */
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

    /**
     * 주어진 이름을 가진 폴더에서 첫 이미지 메타데이터를 반환한다.
     *
     * @param resolver 리스트를 쿼리할 때 사용하는 ContentResolver
     * @return 해당 폴더에서 맨 처음으로 나타나는 이미지 메타데이터
     */
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