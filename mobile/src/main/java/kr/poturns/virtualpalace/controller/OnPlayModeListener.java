package kr.poturns.virtualpalace.controller;

/**
 * Created by YeonhoKim on 2015-09-12.
 */
public interface OnPlayModeListener {
    enum PlayMode {
        STANDARD,
        AR,
        VR
    }


    void onAttached(Long attachedKey);

    void onPlayModeChanged(PlayMode mode, boolean onCardboard);

    void onDetached();
}
