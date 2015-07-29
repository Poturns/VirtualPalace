package kr.poturns.virtualpalace.inputprocessor;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Myungjin Kim on 2015-07-30.
 * <p>
 * 제스쳐를 나타내는 클래스
 */
public class GestureData implements IOutputData {
    public static final int TYPE_TOWARD_UP = 0;
    public static final int TYPE_TOWARD_DOWN = 1;
    public static final int TYPE_TOWARD_LEFT = 2;
    public static final int TYPE_TOWARD_RIGHT = 3;

    public static final int TYPE_TOUCH_TAP = 4;
    public static final int TYPE_TOUCH_PRESS = 5;
    public static final int TYPE_TOUCH_DOWN = 6;

    public static final int AMOUNT_NOTHING = -1;


    /**
     * 제스쳐 형태
     */
    public final int type;
    /**
     * 제스처의 양 (횟수, 길이 등등)
     */
    public final int amount;

    public GestureData(int type, int amount) {
        this.type = type;
        this.amount = amount;
    }

    @Override
    public JSONObject toJsonObject() {
        try {
            return new JSONObject().put("type", type).put("amount", amount);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String toString() {
        return String.format("[ type : %d\namount : %d]", type, amount);
    }
}
