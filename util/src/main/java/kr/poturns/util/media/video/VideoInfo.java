package kr.poturns.util.media.video;


import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Parcel;
import android.provider.MediaStore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kr.poturns.util.IOUtils;
import kr.poturns.util.media.BaseInfo;


public class VideoInfo extends BaseInfo {
    public static final String[] PROJECTION = {
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DURATION
    };

    public long size;
    public long duration;

    public VideoInfo() {
        super();
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

    public static String getJSONInfoList(Context context, String dirName) {
        List<VideoInfo> list = getInfoList(context, dirName);

        final int N = list.size();
        JSONArray jsonArray = new JSONArray();

        for (int i = 0; i < N; i++) {
            jsonArray.put(list.get(i).toJSON());
        }

        return jsonArray.toString();
    }

    public static VideoInfo fromCursor(Cursor cursor, String dirName) {
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

    public Bitmap getFirstFrameThumbnail() {
        return ThumbnailUtils.createVideoThumbnail(path, MediaStore.Images.Thumbnails.MINI_KIND);
    }

    public static String getFirstFrameThumbnail(String fileName, String path) {
        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Images.Thumbnails.MINI_KIND);

        try {
            return IOUtils.bitmapToFile(fileName, bitmap);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        } finally {
            bitmap.recycle();
        }
    }

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

    public static final Creator<VideoInfo> CREATOR = new Creator<VideoInfo>() {
        public VideoInfo createFromParcel(Parcel source) {
            return new VideoInfo(source);
        }

        public VideoInfo[] newArray(int size) {
            return new VideoInfo[size];
        }
    };
}
