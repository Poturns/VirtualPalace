package kr.poturns.virtualpalace.inputcollector;

import org.json.JSONObject;

/**
 * Created by Myungjin Kim on 2015-07-30.
 *
 * {@link InputCollector}가 출력할 데이터를 정의하는 인터페이스
 */
public interface IOutputData {

    /**
     * 현재 객체를 {@link JSONObject}의 형태로 반환한다.
     *
     * @return 현재 객체의 정보가 담긴 {@link JSONObject}
     */
    JSONObject toJSONObject();
}
