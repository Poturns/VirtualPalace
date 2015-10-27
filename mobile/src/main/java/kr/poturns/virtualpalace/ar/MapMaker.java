package kr.poturns.virtualpalace.ar;

import android.util.Log;

import org.opencv.core.Core;
import org.opencv.core.Mat;

import java.util.ArrayList;

public class MapMaker {
    private static final String LOG_TAG = "MapMaker";
    private Thread jniThread;
    private boolean bStop;
    private boolean bRunning;

    //sensor agents
    private final ArrayList<Mat> imgq;

    static {
        System.loadLibrary("shell");
    }

    public MapMaker() {
        imgq = new ArrayList<Mat>();
    }

    public void setImg(Mat img) {
        synchronized (imgq) {
            Mat mat = img.clone();
            imgq.add(mat);
        }
    }

    public void start() {
        if (bRunning)
            return;
        bStop = false;
        Log.i(LOG_TAG, "AR thread start");
        jniThread = new Thread() {
            @Override
            public void run() {
                super.run();
                bRunning = true;
                long laststamp = 0;
                while (!bStop) {
                    //laststamp = Core.getTickCount();
                    if ((double) (Core.getTickCount() - laststamp) / Core.getTickFrequency() < 0.3) {
                        try {
                            sleep(40);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        continue;
                    }
                    Mat img = null;
                    synchronized (imgq) {
                        while (imgq.size() > 0) {
                            if (img != null)
                                img.release();
                            img = imgq.remove(0);
                        }
                    }
                    if (img == null)
                        continue;
                    long addr = img.getNativeObjAddr();
                    add(addr);
                }
                init();
                while (imgq.size() > 0) {
                    imgq.remove(0).release();
                }
                bRunning = false;
                Log.i(LOG_TAG, "AR thread stop");
            }
        };
        jniThread.start();
    }

    public void stop() {
        if (!bRunning)
            return;

        bStop = true;
    }

    public boolean isRunning() {
        return bRunning;
    }

    public static native void init();

    public static native long add(long addr_img);

    public static native int draw(long addr_img);

    public static native int gettrailsize();
}
