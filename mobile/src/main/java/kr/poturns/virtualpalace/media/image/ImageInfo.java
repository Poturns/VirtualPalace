package kr.poturns.virtualpalace.media.image;


import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import kr.poturns.virtualpalace.annotation.UnityApi;
import kr.poturns.virtualpalace.media.BaseMediaInfo;

/**
 * 디바이스 내부에 저장된 이미지의 메타데이터
 */
@UnityApi
public class ImageInfo extends BaseMediaInfo {
    public static final String[] PROJECTION = {
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME
    };

    ImageInfo() {
        super();
    }

    /**
     * Cursor에서 ImageInfo를 생성한다.
     *
     * @param cursor  이미지 메타데이터가 기록된 Cursor
     * @param dirName 이미지가 존재하는 폴더 이름
     * @return VideoInfo 객체
     */
    static ImageInfo fromCursor(Cursor cursor, String dirName) {

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
            imageInfo.dirName = dirName;
            return imageInfo;
        }

        return null;
    }

    /**
     * 주어진 이름의 폴더안에 저장된 이미지의 메타데이터를 모두 얻어 JSON 형태로 반환한다.
     *
     * @param context 메타데이터를 쿼리할 때 사용하는 context
     * @param dirName 메타데이터를 조회할 폴더의 이름
     * @return 폴더안에 저장된 이미지의 메타데이터의 리스트
     */
    @UnityApi
    public static String getJSONInfoList(Context context, String dirName) {
        List<ImageInfo> list = getInfoList(context, dirName);

        final int N = list.size();
        JSONArray jsonArray = new JSONArray();

        for (int i = 0; i < N; i++) {
            jsonArray.put(list.get(i).toJSON());
        }

        return jsonArray.toString();
    }

    /**
     * 주어진 이름의 폴더안에 저장된 이미지의 메타데이터를 모두 얻는다.
     *
     * @param context 메타데이터를 조회할 때 사용하는 context
     * @param dirName 메타데이터를 조회할 폴더의 이름
     * @return 폴더안에 저장된 이미지의 메타데이터의 리스트
     */
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


    /**
     * 이미지의 크기가 너무 큰 경우, OOM을 방지하기 위해 이미지를 적절한 크기로 불러온다.
     *
     * @return 이미지를 적절한 크기로 불러온 비트맵 객체
     */
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

    //***** Parcelable Interface *****//

    protected ImageInfo(Parcel in) {
        super(in);
    }

    public static final Parcelable.Creator<ImageInfo> CREATOR = new Parcelable.Creator<ImageInfo>() {
        public ImageInfo createFromParcel(Parcel source) {
            return new ImageInfo(source);
        }

        public ImageInfo[] newArray(int size) {
            return new ImageInfo[size];
        }
    };


}