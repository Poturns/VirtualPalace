package kr.poturns.virtualpalace.inputcollector;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Myungjin Kim on 2015-07-30.
 * <p>
 * MotionEvent로 부터 해석된 제스쳐를 나타내는 클래스
 */
public class MotionData {
    public static final int TYPE_FLING = 0xf000;
    public static final int TYPE_TOUCH = 0x0f00;

    public static final int TYPE_FLING_TOWARD_UP = TYPE_FLING | 0x0001;
    public static final int TYPE_FLING_TOWARD_DOWN = TYPE_FLING | 0x0002;
    public static final int TYPE_FLING_TOWARD_LEFT = TYPE_FLING | 0x0003;
    public static final int TYPE_FLING_TOWARD_RIGHT = TYPE_FLING | 0x0004;

    public static final int TYPE_TOUCH_TAP = TYPE_TOUCH | 0x0001;
    public static final int TYPE_TOUCH_PRESS = TYPE_TOUCH | 0x0002;
    public static final int TYPE_TOUCH_DOWN = TYPE_TOUCH | 0x0003;

    public static final int AMOUNT_NOTHING = -1;


    /**
     * 제스쳐 형태
     */
    public final int type;
    /**
     * 제스처의 양 (횟수, 길이 등등)
     */
    public final int amount;

    public MotionData(int type, int amount) {
        this.type = type;
        this.amount = amount;
    }

    /**
     * 제스쳐가 해당 타입인지 확인한다.
     *
     * @param generalType {@link MotionData#TYPE_FLING} 또는 {@link MotionData#TYPE_TOUCH}
     * @return 해당 타입인지 여부
     */
    public boolean isTypeOf(int generalType) {
        return (type & generalType) == generalType;
    }

    public JSONObject toJSONObject() {
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
