package kr.poturns.virtualpalace.input;

/**
 *
 * @author Yeonho.Kim
 */
public interface IControllerCommands {

    // * * * S U P P O R T  F L A G * * * //
    /**
     *
     */
    public static final int TYPE_INPUT_SUPPORT_SCREENTOUCH = 0x1;
    /**
     *
     */
    public static final int TYPE_INPUT_SUPPORT_SCREENFOCUS = 0x2;
    /**
     *
     */
    public static final int TYPE_INPUT_SUPPORT_MOTION = 0x4;
    /**
     *
     */
    public static final int TYPE_INPUT_SUPPORT_VOICE = 0x10;
    /**
     *
     */
    public static final int TYPE_INPUT_SUPPORT_WATCH = 0x20;



    // * * * C O M M A N D S * * * //
    /**
     *
     */
    public static final int INPUT_SYNC_COMMAND = 0x10;
    /**
     *
     */
    public static final int INPUT_SINGLE_COMMAND = 0x11;
    /**
     *
     */
    public static final int INPUT_MULTI_COMMANDS = 0x12;


    /**
     *
     */
    public static final int REQUEST_MESSAGE_FROM_UNITY = 0x10;
    /**
     *
     */
    public static final int REQUEST_CALLBACK_FROM_UNITY = 0x11;

}
