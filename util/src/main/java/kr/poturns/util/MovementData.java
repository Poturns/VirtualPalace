package kr.poturns.util;


import java.io.Serializable;

public class MovementData implements Serializable {
    private static final long serialVersionUID = -3839711287376500586L;

    public final float x, y, z, speed;

    public MovementData(float x, float y, float z, float speed) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.speed = speed;
    }

    @Override
    public String toString() {
        return String.format("[ x : %.5f\ny : %.5f\nz : %.5f\nspeed : %.5f]", x, y, z, speed);
    }
}
