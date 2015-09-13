package kr.poturns.virtualpalace.controller;

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
     *      /sdcard/ VirtualPalace/
     */
    public static final String BASE_DIR = "VirtualPalace";
    /**
     * 외부저장소 : 로그 디렉토리
     *      /sdcard/ VirtualPalace/ Logs/
     */
    public static final String LOG_DIR = BASE_DIR + File.pathSeparator + "Logs";
    /**
     * 외부저장소 : 다운로드 디렉토리
     *      /sdcard/ VirtualPalace/ Downloads/
     */
    public static final String DOWNLOAD_DIR = BASE_DIR + File.pathSeparator + "Downloads";
    /**
     * 내부저장소 : 시스템 디렉토리
     *      /data/data/kr.poturns.virtualpalace/ System/
     */
    public static final String SYSTEM_DIR = "System";

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
    /**
     *
     */
    private void ready() {
        File logDir = new File(Environment.getExternalStorageDirectory(), LOG_DIR);
        logDir.mkdirs();

        File downloadDir = new File(Environment.getExternalStorageDirectory(), DOWNLOAD_DIR);
        logDir.mkdirs();

        File systemDir = new File (SYSTEM_DIR);
        systemDir.mkdirs();
    }

    /**
     *
     * @param logShard
     */
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

    /**
     *
     * @param name
     * @param overwrite
     * @throws NoSuchFileException 해당 파일이 이미 존재하고 Overwrite 가 아닐 경우 발생.
     */
    public void saveFileIntoDownloads(String name, boolean overwrite) throws NoSuchFileException {
        File downloads = new File(Environment.getExternalStorageDirectory(), DOWNLOAD_DIR);
        File target = new File(downloads, name);
        if (!target.exists() || overwrite) {
            // TODO : Write

        } else
            throw new NoSuchFileException("The file already exists. If you want to continue, set OVERWRITE flag.");
    }

    /**
     *
     * @param name
     * @throws NoSuchFileException 파일이 존재하지 않을 경우 발생.
     */
    public void loadFileFromDownloads(String name) throws NoSuchFileException {
        File downloads = new File(Environment.getExternalStorageDirectory(), DOWNLOAD_DIR);
        File target = new File(downloads, name);
        if (target.exists()) {
            // TODO : Read

        } else
            throw new NoSuchFileException("The file doesn't exist.");
    }

    /**
     *
     * @param name
     * @param overwrite
     * @throws NoSuchFileException 해당 파일이 이미 존재하고 Overwrite 가 아닐 경우 발생.
     */
    public void saveFileIntoSystem(String name, boolean overwrite) throws NoSuchFileException {
        File target = new File(new File(SYSTEM_DIR), name);
        if (!target.exists() || overwrite) {
            // TODO : Write

        } else
            throw new NoSuchFileException("The file already exists. If you want to continue, set OVERWRITE flag.");
    }

    /**
     *
     * @param name
     * @throws NoSuchFileException 파일이 존재하지 않을 경우 발생.
     */
    public void loadFileFromSystem(String name) throws NoSuchFileException  {
        File target = new File(new File(SYSTEM_DIR), name);
        if (target.exists()) {
            // TODO : Read

        } else
            throw new NoSuchFileException("The file doesn't exist.");
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
        String ACCOUNT = "local_archive_system_account";
    }


    /**
     *
     */
    public static class NoSuchFileException extends Exception {
        public NoSuchFileException(String message) {
            super(message);
        }
    }

}
