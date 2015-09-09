package kr.poturns.virtualpalace.media;


import android.content.Context;

import org.json.JSONObject;

import java.util.List;

/**
 * 미디어의 폴더 메타데이터
 */
public abstract class BaseMediaDirInfo<T extends BaseMediaInfo> extends MediaInfo {
    /**
     * 폴더에 존재하는 미디어 중, 첫번 째 미디어
     */
    public T firstInfo;


    /**
     * 폴더에 존재하는 미디어의 메타데이터 리스트를 불러온다.
     *
     * @param context 리스트를 쿼리할 때 사용하는 context
     * @return 폴더에 존재하는 미디어의 메타데이터 리스트
     */
    public abstract List<T> getInfoList(Context context);

    @Override
    public JSONObject toJSON() {
        try {
            return new JSONObject()
                    .put("dirName", dirName)
                    .put("firstInfo", firstInfo.toJSON());

        } catch (Exception e) {
            return new JSONObject();
        }
    }

}
