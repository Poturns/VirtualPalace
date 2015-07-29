package kr.poturns.virtualpalace.inputprocessor;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Myungjin Kim on 2015-07-30.
 * <p>
 * 센서의 움직임을 나타내는 클래스
 */
public class SensorMovementData implements IOutputData {
    public final float x, y, z, speed;

    public SensorMovementData(float x, float y, float z, float speed) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.speed = speed;
    }

    @Override
    public String toString() {
        return String.format("[ x : %.5f\ny : %.5f\nz : %.5f\nspeed : %.5f]", x, y, z, speed);
    }

    @Override
    public JSONObject toJsonObject() {
        try {
            return new JSONObject().put("x", x).put("y", y).put("z", z).put("speed", speed);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
