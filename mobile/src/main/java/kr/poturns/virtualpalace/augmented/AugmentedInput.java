package kr.poturns.virtualpalace.augmented;

/**
 * Controller 에서 전달받는 AR 연산 입력 데이터
 */
public class AugmentedInput {
    public AugmentedInput() {

    }

    public int augmentedID;
    public int resID;

    public double altitude;
    public double latitude;
    public double longitude;

    public double supportX;
    public double supportY;
    public double supportZ;


    public AugmentedOutput extractOutput(int screenX, int screenY) {
        AugmentedOutput output = new AugmentedOutput();

        output.screenX = screenX;
        output.screenY = screenY;
        output.resID = this.resID;

        return output;
    }
}
