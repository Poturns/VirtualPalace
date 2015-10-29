package kr.poturns.virtualpalace.augmented;

/**
 * Created by Yeonho on 2015-10-25.
 */
public class AugmentedItem {
    public AugmentedItem() {

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
