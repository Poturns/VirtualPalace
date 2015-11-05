package kr.poturns.virtualpalace.augmented;

/**
 * Controller 에서 전달받는 AR 연산 입력 데이터
 */
public class AugmentedItem {
    public static final String SCREEN_X = "screen_x";
    public static final String SCREEN_Y = "screen_y";

    public AugmentedItem() {

    }

    public long augmentedID;
    public long resID;

    public double altitude;
    public double latitude;
    public double longitude;

    public double supportX;
    public double supportY;
    public double supportZ;

    public String title;
    public String contents;
    public int res_type;

    public int screenX;
    public int screenY;


    public AugmentedOutput extractOutput(int screenX, int screenY) {
        AugmentedOutput output = new AugmentedOutput();

        output.screenX = screenX;
        output.screenY = screenY;
        output.resID = this.resID;

        return output;
    }
}
