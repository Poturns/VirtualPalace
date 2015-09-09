package kr.poturns.virtualpalace.util;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Date;

import kr.poturns.virtualpalace.annotation.UnityApi;

/**
 * Created by Myungjin Kim on 2015-07-30.
 * <p/>
 * 구글 드라이브와의 작업을 처리하는 클래스
 */
@UnityApi
public class DriveAssistant implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "DriveAssistant";

    private GoogleApiClient mGoogleApiClient;
    private final Context context;

    public DriveAssistant(Context context) {
        this.context = context;

        initApiClient();
    }

    private void initApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addScope(Drive.SCOPE_APPFOLDER)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    //**** lifecycle method ****

    /**
     * GoogleApiClient와 연결 여부를 확인한다.
     *
     * @return 연결된 경우 true
     */
    public boolean isConnected() {
        return mGoogleApiClient.isConnected();
    }

    /**
     * GoogleApiClient와의 연결을 요청한다.
     * <p/>
     * 연결 작업은 비동기적으로 수행된다.
     */
    public final void connect() {
        if (mGoogleApiClient != null && !mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();
    }

    /**
     * GoogleApiClient 와 연결한다. 연결 요청은 동기적으로 수행된다.
     */
    public final ConnectionResult blockingConnect() {
        return mGoogleApiClient.blockingConnect();
    }

    /**
     * GoogleApiClient 와 연결 해제한다.
     */
    public final void disconnect() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
    }

    /**
     * GoogleApiClient 와 연결 해제하고, 리소스를 정리한다.
     */
    public final void destroy() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.unregisterConnectionCallbacks(this);
            mGoogleApiClient.unregisterConnectionFailedListener(this);
            mGoogleApiClient.disconnect();
            mGoogleApiClient = null;
        }
    }

    // ****  GoogleApiClient.ConnectionCallback ****

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected : " + bundle);
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.e(TAG, "onConnectionSuspended : " + cause);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.e(TAG, result.toString());
        if (!result.hasResolution()) {
            // show the localized error dialog.
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), (Activity) context, 0).show();
            return;
        }
        try {
            result.startResolutionForResult((Activity) context, 1000);
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "Exception while starting resolution activity", e);
        }
    }

    //****

    /**
     * DriveFile을 여는 방식을 제한하는 Annotation
     */
    @IntDef(value = {DriveFile.MODE_READ_ONLY, DriveFile.MODE_WRITE_ONLY, DriveFile.MODE_READ_WRITE})
    @Retention(RetentionPolicy.SOURCE)
    @interface FileMode {
    }


    //**** Drive API ****


    /**
     * 파일을 open한다.
     *
     * @param id   open할 파일의 id
     * @param mode open mode, 아래 모드 중 하나.
     *             <li>{@link DriveFile#MODE_READ_ONLY} : 파일을 읽기 전용으로 open한다.</li>
     *             <li>{@link DriveFile#MODE_WRITE_ONLY} : 파일을 쓰기 전용으로 open한다. commit 한 경우 기존에 기록된 내용을 사라진다.</li>
     *             <li>{@link DriveFile#MODE_READ_WRITE} : 파일을 append 모드로 open한다. commit 한 경우 기존에 기록된 내용에 덧붙여진다.</li>
     * @return 파일의 내용
     */
    @UnityApi
    public DriveContents openFile(DriveId id, @FileMode int mode) {
        return openFile(getFile(id), mode);
    }

    /**
     * 파일을 가져온다.
     *
     * @param id 파일의 id
     * @return 주어진 id에 해당하는 파일
     */
    @UnityApi
    public DriveFile getFile(DriveId id) {
        return Drive.DriveApi.getFile(mGoogleApiClient, id);
    }

    /**
     * Drive의 최상위 폴더를 가져온다.
     *
     * @return Drive의 최상위 폴더
     */
    @UnityApi
    public DriveFolder getRootFolder() {
        return Drive.DriveApi.getRootFolder(mGoogleApiClient);
    }

    /**
     * 폴더를 가져온다.
     *
     * @param id 폴더의 id
     * @return 주어진 id에 해당하는 폴더
     */
    @UnityApi
    public DriveFolder getFolder(DriveId id) {
        return Drive.DriveApi.getFolder(mGoogleApiClient, id);
    }


    /**
     * App-private한 폴더를 가져온다.
     *
     * @return 폴더
     */
    @UnityApi
    public DriveFolder getAppFolder() {
        return Drive.DriveApi.getAppFolder(mGoogleApiClient);
    }

    /**
     * 새로운 DriveContents를 생성한다.
     *
     * @return 생성된 DriveContents
     */
    @UnityApi
    public DriveContents newDriveContents() {
        return get(Drive.DriveApi.newDriveContents(mGoogleApiClient).await());
    }

    /**
     * 존재하는 파일의 정보를 가져온다.
     *
     * @param title 검색할 이름
     * @return 조건에 알맞는 파일의 정보
     */
    @UnityApi
    public DriveApi.MetadataBufferResult queryByTitle(String title) {
        return query(
                new Query.Builder()
                        .addFilter(Filters.eq(SearchableField.TITLE, title))
                        .build()
        );

    }

    /**
     * 존재하는 파일의 정보를 가져온다.
     *
     * @param mimeType 검색할 MimeType
     * @return 조건에 알맞는 파일의 정보
     */
    @UnityApi
    public DriveApi.MetadataBufferResult queryByMimeType(String mimeType) {
        return query(
                new Query.Builder()
                        .addFilter(Filters.eq(SearchableField.MIME_TYPE, mimeType))
                        .build()
        );
    }

    /**
     * 주어진 쿼리로 드라이브에 쿼리한다.
     */
    private DriveApi.MetadataBufferResult query(Query query) {
        return Drive.DriveApi.query(mGoogleApiClient, query).await();
    }

    // ****


    //****** 아래의 API 메소드들은 Unity에서의 호출의 편의성을 위해 DriveHelper에 작성하였음. ******//

    //**** DriveFile API ****


    /**
     * 파일을 open한다.
     *
     * @param driveFile open할 파일
     * @param mode      open mode, 아래 모드 중 하나.
     *                  <li>{@link DriveFile#MODE_READ_ONLY} : 파일을 읽기 전용으로 open한다.</li>
     *                  <li>{@link DriveFile#MODE_WRITE_ONLY} : 파일을 쓰기 전용으로 open한다. commit 한 경우 기존에 기록된 내용을 사라진다.</li>
     *                  <li>{@link DriveFile#MODE_READ_WRITE} : 파일을 append 모드로 open한다. commit 한 경우 기존에 기록된 내용에 덧붙여진다.</li>
     * @return 파일의 내용
     */
    @UnityApi
    public DriveContents openFile(DriveFile driveFile, int mode) {
        return get(driveFile.open(mGoogleApiClient, mode, null).await());
    }

    /**
     * 파일을 삭제한다.
     *
     * @param driveFile 삭제할 파일
     * @return 결과
     */
    @UnityApi
    public Status deleteFile(DriveFile driveFile) {
        return driveFile.delete(mGoogleApiClient).await();
    }

    //*****


    //**** DriveFolder API ****


    /**
     * 파일을 생성한다.
     *
     * @param folder   파일이 생성될 DriveFolder
     * @param contents 파일의 내용
     * @param title    파일 이름
     * @param mimeType 파일의 MimeType
     * @return 생성된 파일
     */
    @UnityApi
    public DriveFile createFile(DriveFolder folder, DriveContents contents, String title, String mimeType) {
        MetadataChangeSet.Builder builder = new MetadataChangeSet.Builder();

        if (title != null)
            builder.setTitle(title);
        if (mimeType != null)
            builder.setMimeType(mimeType);


        return get(folder.createFile(mGoogleApiClient, builder.build(), contents).await());
    }

    /**
     * 폴더를 생성한다.
     *
     * @param folder 폴더가 생성될 DriveFolder
     * @param title  폴더의 이름
     * @return 생성된 폴더
     */
    @UnityApi
    public DriveFolder createFolder(DriveFolder folder, String title) {
        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                .setTitle(title)
                .build();

        return get(folder.createFolder(mGoogleApiClient, changeSet).await());
    }

    /**
     * 폴더에 존재하는 모든 파일의 정보를 가져온다.
     *
     * @param folder 파일 정보를 가져올 폴더
     * @return 폴더에 존재하는 모든 파일의 정보
     */
    @UnityApi
    public MetadataBuffer listChildren(DriveFolder folder) {
        return get(folder.listChildren(mGoogleApiClient).await());
    }

    /**
     * 폴더에 존재하는 파일의 정보를 가져온다.
     *
     * @param folder 파일의 정보를 가져올 폴더
     * @param title  검색할 이름
     * @return 조건에 알맞는 파일의 정보
     */
    @UnityApi
    public MetadataBuffer queryChildrenByTitle(DriveFolder folder, String title) {
        return get(folder.queryChildren(mGoogleApiClient,
                new Query.Builder()
                        .addFilter(Filters.eq(SearchableField.TITLE, title))
                        .build()
        ).await());
    }

    /**
     * 폴더에 존재하는 파일의 정보를 가져온다.
     *
     * @param folder   파일의 정보를 가져올 폴더
     * @param mimeType 검색할 MimeType
     * @return 조건에 알맞는 파일의 정보
     */
    @UnityApi
    public MetadataBuffer queryChildrenByMimeType(DriveFolder folder, String mimeType) {
        return get(folder.queryChildren(mGoogleApiClient,
                new Query.Builder()
                        .addFilter(Filters.eq(SearchableField.MIME_TYPE, mimeType))
                        .build()
        ).await());
    }

    //*****


    //***** DriveContents API ****

    /**
     * DriveFile의 변경된 내용을 기록한다.
     *
     * @param contents 변경된 내용이 기록된 DriveContents
     * @return 결과
     */
    @UnityApi
    public Status commit(DriveContents contents) {
        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                .setLastViewedByMeDate(new Date())
                .build();

        return contents.commit(mGoogleApiClient, changeSet).await();
    }

    /**
     * DriveFile의 변경된 내용을 기록한다.
     *
     * @param contents 변경된 내용이 기록된 DriveContents
     * @param title    DriveFile 이름
     * @return 결과
     */
    @UnityApi
    public Status commit(DriveContents contents, String title) {
        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                .setTitle(title)
                .setLastViewedByMeDate(new Date())
                .build();

        return contents.commit(mGoogleApiClient, changeSet).await();
    }


    /**
     * DriveFile의 변경된 내용을 폐기한다.
     */
    @UnityApi
    public void discard(DriveContents contents) {
        contents.discard(mGoogleApiClient);
    }


    /**
     * DriveContents에서 기록된 내용을 가져온다.
     *
     * @param contents 내용을 가져올 DriveContents
     * @return DriveContents에 기록된 내용
     */
    @UnityApi
    public static byte[] openContents(DriveContents contents) {
        InputStream in = determineInputStream(contents);
        if (in == null)
            return null;

        try {
            return IOUtils.readContent(in);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * DriveContents에서 기록된 내용을 가져온다.
     *
     * @param contents 내용을 가져올 DriveContents
     * @param encoding 반환될 문자열의 인코딩
     * @return DriveContents에 기록된 문자열
     */
    @UnityApi
    public static String openStringContents(DriveContents contents, String encoding) {
        InputStream in = determineInputStream(contents);
        if (in == null)
            return null;

        try {
            return IOUtils.readStringContent(in, encoding);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * DriveContents에서 기록된 내용을 가져온다.
     *
     * @param context      임시 파일을 생성하기 위한 용도
     * @param contents     내용을 가져올 DriveContents
     * @param tempFileName 가져온 내용이 기록 될 임시 파일의 이름
     * @return DriveContents의 내용이 기록된 파일의 경로
     */
    @UnityApi
    public static String openContents(Context context, DriveContents contents, String tempFileName) {
        InputStream in = determineInputStream(contents);
        if (in == null)
            return null;

        try {
            File f = File.createTempFile(tempFileName, null, context.getCacheDir());
            IOUtils.readContentToFile(in, f);
            return f.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * DriveContents에 내용을 기록한다.
     *
     * @param contents 내용이 기록될 DriveContents
     * @param s        기록할 내용
     * @return 성공 여부
     */
    @UnityApi
    public static boolean writeStringContents(DriveContents contents, String s) {
        OutputStream out = determineOutputStream(contents);
        if (out == null)
            return false;

        try {
            IOUtils.writeContentToStream(out, s);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * DriveContents 에 내용을 기록한다.
     *
     * @param contents 내용이 기록될 DriveContents
     * @param bytes    기록할 내용
     * @return 성공 여부
     */
    @UnityApi
    public static boolean writeContents(DriveContents contents, byte[] bytes) {
        OutputStream out = determineOutputStream(contents);
        if (out == null)
            return false;

        try {
            IOUtils.writeContentToStream(out, bytes);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * DriveContents 에 내용을 기록한다.
     *
     * @param contents 내용이 기록될 DriveContents
     * @param filePath 기록할 내용이 존재하는 파일의 경로
     * @return 성공 여부
     */
    @UnityApi
    public static boolean writeFileContents(DriveContents contents, String filePath) {
        OutputStream out = determineOutputStream(contents);
        if (out == null)
            return false;

        try {
            IOUtils.writeContentToStream(out, new File(filePath));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * DriveFile이 열려진 방식에 따라 InputStream을 결정한다.
     */
    private static InputStream determineInputStream(DriveContents contents) {
        switch (contents.getMode()) {
            case DriveFile.MODE_READ_ONLY:
                return contents.getInputStream();
            case DriveFile.MODE_READ_WRITE:
                return new FileInputStream(contents.getParcelFileDescriptor().getFileDescriptor());

            default:
                return null;
        }
    }

    /**
     * DriveFile이 열려진 방식에 따라 OutputStream을 결정한다.
     */
    private static OutputStream determineOutputStream(DriveContents contents) {
        switch (contents.getMode()) {
            case DriveFile.MODE_WRITE_ONLY:
                return contents.getOutputStream();

            case DriveFile.MODE_READ_WRITE:
                return new FileOutputStream(contents.getParcelFileDescriptor().getFileDescriptor());

            default:
                return null;
        }
    }

    //*****


    // internal util method

    private static DriveContents get(DriveApi.DriveContentsResult result) {
        return result.getStatus().isSuccess() ? result.getDriveContents() : null;
    }

    private static DriveFile get(DriveFolder.DriveFileResult result) {
        return result.getStatus().isSuccess() ? result.getDriveFile() : null;
    }

    private static DriveFolder get(DriveFolder.DriveFolderResult result) {
        return result.getStatus().isSuccess() ? result.getDriveFolder() : null;
    }

    private static MetadataBuffer get(DriveApi.MetadataBufferResult result) {
        return result.getStatus().isSuccess() ? result.getMetadataBuffer() : null;
    }

}

