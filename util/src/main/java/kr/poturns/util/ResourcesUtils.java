package kr.poturns.util;


import android.content.Context;

public class ResourcesUtils {
    public static String[] get(Context context, String resName) {
        return context.getResources().getStringArray(context.getResources().getIdentifier(resName, "array", context.getPackageName()));
    }
}
