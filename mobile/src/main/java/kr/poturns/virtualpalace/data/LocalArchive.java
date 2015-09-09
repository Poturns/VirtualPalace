package kr.poturns.virtualpalace.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.text.format.DateFormat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * <b> 로컬 저장소에 파일로서 데이터를 관리한다. </b>
 * <p>
 *     보안 및 관리 용이성을 위하여 로컬 저장소를 내부/외부 저장소로 나누어 활용한다.
 *
 * </p>
 *
 * @author Yeonho.Kim
 */
public class LocalArchive  {

    // * * * S I N G L E T O N * * * //
    private static LocalArchive sInstance;

    public static final LocalArchive getInstance(Context context) {
        if (sInstance == null)
            sInstance = new LocalArchive(context);
        return sInstance;
    }



    // * * * C O N S T A N T S * * * //
    public static final String NAME = "LocalArchive";
    /**
     * 외부저장소 : 앱 기본 디렉토리
     *      /sdcard/ VirtualPalace
     */
    public static final String BASE_DIR = "VirtualPalace";
    /**
     * 외부저장소 : 로그 디렉토리
     *      /sdcard/ VirtualPalace/ Logs
     */
    public static final String LOG_DIR = BASE_DIR + File.pathSeparator + "Logs";
    /**
     * 내부저장소 : 다운로드 디렉토리
     *      /data/data/kr.poturns.virtualpalace/ VirtualPalace/ Downloads
     */
    public static final String DOWNLOAD_DIR = BASE_DIR + File.pathSeparator + "Downloads";


    private final Context mContextF;

    private final SharedPreferences mPrefF;



    // * * * F I E L D S * * * //



    // * * * C O N S T R U C T O R S * * * //
    private LocalArchive(Context context) {
        mContextF = context;
        mPrefF = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);

        ready();
    }



    // * * * M E T H O D S * * * //
    private void ready() {
        File logDir = new File(Environment.getExternalStorageDirectory(), LOG_DIR);
        logDir.mkdirs();

        File downloadDir = new File (Environment.getDataDirectory(), DOWNLOAD_DIR);
        downloadDir.mkdirs();
    }

    public synchronized void appendLog(LogShard logShard) {
        if (logShard == null)
            return;

        File logDir = new File(Environment.getExternalStorageDirectory(), LOG_DIR);
        File logFile = new File(logDir, DateFormat.format("yyyy-MM-dd", logShard.time).toString());

        BufferedWriter writer = null;
        try {
            if (!logFile.exists())
                logFile.createNewFile();

            writer = new BufferedWriter(new FileWriter(logFile, true));
            writer.append(logShard.toString());
            writer.newLine();

        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            if (writer != null) {
                try {
                    writer.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void saveFileIntoDownloads(String name) {
        File downloads = new File(Environment.getDataDirectory(), DOWNLOAD_DIR);
        File target = new File(downloads, name);

    }

    public void loadFileFromDownloads(String name) {
        File downloads = new File(Environment.getDataDirectory(), DOWNLOAD_DIR);
        File target = new File(downloads, name);

    }



    // * * * G E T T E R S & S E T T E R S * * * //
    public String getSystemStringValue(String key) {
        return mPrefF.getString(key, null);
    }

    public boolean putSystemStringValue(String key, String value) {
        return mPrefF.edit()
                    .putString(key, value)
                    .commit();
    }



    // * * * I N N E R  C L A S S E S * * * //
    public interface ISystem {
        public static final String ACCOUNT = "local_archive_system_account";
    }



}
