package kr.poturns.virtualpalace.media.video;


import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kr.poturns.virtualpalace.annotation.UnityApi;
import kr.poturns.virtualpalace.media.BaseMediaInfo;
import kr.poturns.virtualpalace.util.IOUtils;

/**
 * 디바이스 내부에 저장된 비디오의 메타데이터
 */
@UnityApi
public class VideoInfo extends BaseMediaInfo {
    public static final String[] PROJECTION = {
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DURATION
    };

    /**
     * 비디오의 크기
     */
    public long size;
    /**
     * 비디오의 재생 시간
     */
    public long duration;

    VideoInfo() {
        super();
    }

    /**
     * Cursor에서 VideoInfo를 생성한다.
     *
     * @param cursor  비디오 메타데이터가 기록된 Cursor
     * @param dirName 비디오가 존재하는 폴더 이름
     * @return VideoInfo 객체
     */
    static VideoInfo fromCursor(Cursor cursor, String dirName) {
        int idIndex = cursor.getColumnIndex(PROJECTION[0]);
        int pathIdx = cursor.getColumnIndex(PROJECTION[1]);
        int nameIdx = cursor.getColumnIndex(PROJECTION[2]);
        int sizeIdx = cursor.getColumnIndex(PROJECTION[3]);
        int durationIdx = cursor.getColumnIndex(PROJECTION[4]);

        int id = cursor.getInt(idIndex);
        String imagePath = cursor.getString(pathIdx);
        String displayName = cursor.getString(nameIdx);
        long size = cursor.getLong(sizeIdx);
        long duration = cursor.getLong(durationIdx);

        if (displayName == null)
            displayName = imagePath.substring(imagePath.lastIndexOf('/') + 1);

        VideoInfo info = new VideoInfo();
        info.id = id;
        info.path = imagePath;
        info.displayName = displayName;
        info.dirName = dirName;
        info.size = size;
        info.duration = duration;
        return info;
    }

    @Override
    public JSONObject toJSON() {
        try {
            return super.toJSON()
                    .put("size", size)
                    .put("duration", duration);
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }

    /**
     * 주어진 이름의 폴더안에 저장된 비디오의 메타데이터를 모두 얻어 JSON 형태로 반환한다.
     *
     * @param context 메타데이터를 조회할 때 사용하는 context
     * @param dirName 메타데이터를 조회할 폴더의 이름
     * @return 폴더안에 저장된 비디오의 메타데이터의 리스트
     */
    @UnityApi
    public static String getJSONInfoList(Context context, String dirName) {
        List<VideoInfo> list = getInfoList(context, dirName);

        final int N = list.size();
        JSONArray jsonArray = new JSONArray();

        for (int i = 0; i < N; i++) {
            jsonArray.put(list.get(i).toJSON());
        }

        return jsonArray.toString();
    }

    /**
     * 주어진 이름의 폴더안에 저장된 비디오의 메타데이터를 모두 얻는다.
     *
     * @param context 메타데이터를 조회할 때 사용하는 context
     * @param dirName 메타데이터를 조회할 폴더의 이름
     * @return 폴더안에 저장된 비디오의 메타데이터의 리스트
     */
    public static List<VideoInfo> getInfoList(Context context, String dirName) {
        String[] selectionArgs = {dirName};
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                VideoInfo.PROJECTION,
                MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME + "=?",
                selectionArgs,
                MediaStore.Video.Media.DISPLAY_NAME
        );

        ArrayList<VideoInfo> infoArrayList = new ArrayList<VideoInfo>();

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    VideoInfo info = VideoInfo.fromCursor(cursor, dirName);
                    if (info != null) {
                        infoArrayList.add(info);
                    }
                }
                while (cursor.moveToNext());

                cursor.close();
            }
        }

        return infoArrayList;
    }

    /**
     * 비디오의 처음 프레임의 썸네일을 얻는다.
     *
     * @return 비디오의 처음 프레임의 썸네일를 나타내는 비트맵
     */
    public Bitmap getFirstFrameThumbnail() {
        return ThumbnailUtils.createVideoThumbnail(path, MediaStore.Images.Thumbnails.MINI_KIND);
    }

    /**
     * 비디오의 처음 프레임의 썸네일을 얻는다.
     *
     * @param context  앱의 임시 폴더를 얻어올 때 사용한다.
     * @param fileName 썸네일이 저장될 파일의 이름
     * @param path     비디오가 저장된 경로
     * @return 비디오의 처음 프레임의 썸네일를 나타내는 비트맵이 저장된 경로
     */
    @UnityApi
    public static String getFirstFrameThumbnail(Context context, String fileName, String path) {
        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Images.Thumbnails.MINI_KIND);

        try {
            return IOUtils.bitmapToFile(context, fileName, bitmap);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        } finally {
            bitmap.recycle();
        }
    }


    //***** Parcelable Interface *****//

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(size);
        dest.writeLong(duration);
    }

    protected VideoInfo(Parcel in) {
        super(in);
        size = in.readLong();
        duration = in.readLong();
    }

    public static final Parcelable.Creator<VideoInfo> CREATOR = new Parcelable.Creator<VideoInfo>() {
        public VideoInfo createFromParcel(Parcel source) {
            return new VideoInfo(source);
        }

        public VideoInfo[] newArray(int size) {
            return new VideoInfo[size];
        }
    };
}
