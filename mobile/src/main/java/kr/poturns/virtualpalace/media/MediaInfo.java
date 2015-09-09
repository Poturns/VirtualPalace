package kr.poturns.virtualpalace.media;


import org.json.JSONObject;

/**
 * 디바이스 내부에 존재하는 미디어(음악, 비디오 등등)의 메타데이터
 */
public abstract class MediaInfo {
    /**
     * 미디어가 있는 폴더의 이름
     */
    public String dirName;

    /**
     * 객체를 JSON 형태로 변환한다.
     */
    public abstract JSONObject toJSON();

    /**
     * JSON 형식으로 객체를 출력한다.
     */
    @Override
    public String toString() {
        return toJSON().toString();
    }

}
