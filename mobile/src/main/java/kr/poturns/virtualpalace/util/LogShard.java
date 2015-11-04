package kr.poturns.virtualpalace.util;


import android.text.format.DateFormat;

/**
 * <b> </b>
 *
 * @author Yeonho.Kim
 */
public class LogShard {

    // * * * F I E L D S * * * //
    final long time;

    String action;




    // * * * C O N S T R U C T O R S * * * //
    public LogShard(String action) {
        this.time = System.currentTimeMillis();

        this.action = action;
    }



    // * * * I N H E R I T S * * * //
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(DateFormat.format("yy/MM/dd HH:mm:ss", time)).append('\t');
        builder.append(action).append('\t');

        return builder.toString();
    }
}
