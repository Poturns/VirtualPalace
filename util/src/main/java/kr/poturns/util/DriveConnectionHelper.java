package kr.poturns.util;


import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Base64;
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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

public class DriveConnectionHelper extends InputHandleHelper.ContextInputHandleHelper implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "DriveConnectionHelper";
    private GoogleApiClient mGoogleApiClient;

    public DriveConnectionHelper(Context context) {
        super(context);

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

    public boolean isConnected(){
        return mGoogleApiClient.isConnected();
    }

    @Override
    public final void resume() {
        if (mGoogleApiClient != null && !mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();
    }

    @Override
    public final void pause() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
    }

    @Override
    public final void destroy() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
            mGoogleApiClient = null;
        }
    }

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

    public DriveContents openFile(DriveId id, int mode) {
        return openFile(getFile(id), mode);
    }


    public DriveFile getFile(DriveId id) {
        return Drive.DriveApi.getFile(mGoogleApiClient, id);
    }


    public DriveFolder getRootFolder() {
        return Drive.DriveApi.getRootFolder(mGoogleApiClient);
    }

    public DriveFolder getFolder(DriveId id) {
        return Drive.DriveApi.getFolder(mGoogleApiClient, id);
    }

    public DriveFolder getAppFolder() {
        return Drive.DriveApi.getAppFolder(mGoogleApiClient);
    }

    public DriveContents newDriveContents() {
        return get(Drive.DriveApi.newDriveContents(mGoogleApiClient).await());
    }


    public DriveApi.MetadataBufferResult queryByTitle(String title) {
        return query(
                new Query.Builder()
                        .addFilter(Filters.eq(SearchableField.TITLE, title))
                        .build()
        );

    }

    public DriveApi.MetadataBufferResult queryByMimeType(String mimeType) {
        return query(
                new Query.Builder()
                        .addFilter(Filters.eq(SearchableField.MIME_TYPE, mimeType))
                        .build()
        );
    }

    private DriveApi.MetadataBufferResult query(Query query) {
        return Drive.DriveApi.query(mGoogleApiClient, query).await();
    }


    //**** DriveFile API

    public DriveContents openFile(DriveFile driveFile, int mode) {
        return get(driveFile.open(mGoogleApiClient, mode, null).await());
    }

    public Status deleteFile(DriveFile driveFile) {
        return driveFile.delete(mGoogleApiClient).await();
    }

    //*****

    //**** DriveFolder API

    public DriveFile createFile(DriveFolder folder, String title, String mimeType, DriveContents contents) {
        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                .setTitle(title)
                .setMimeType(mimeType)
                .build();

        return get(folder.createFile(mGoogleApiClient, changeSet, contents).await());
    }

    public DriveFolder createFolder(DriveFolder folder, String title) {
        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                .setTitle(title)
                .build();

        return get(folder.createFolder(mGoogleApiClient, changeSet).await());
    }


    public MetadataBuffer listChildren(DriveFolder folder) {
        return get(folder.listChildren(mGoogleApiClient).await());
    }

    public MetadataBuffer queryChildrenByTitle(DriveFolder folder, String title) {
        return get(folder.queryChildren(mGoogleApiClient,
                new Query.Builder()
                        .addFilter(Filters.eq(SearchableField.TITLE, title))
                        .build()
        ).await());
    }

    public MetadataBuffer queryChildrenByMimeType(DriveFolder folder, String mimeType) {
        return get(folder.queryChildren(mGoogleApiClient,
                new Query.Builder()
                        .addFilter(Filters.eq(SearchableField.MIME_TYPE, mimeType))
                        .build()
        ).await());
    }

    //*****


    //***** DriveContents API

    public Status commit(DriveContents contents) {
        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                .setLastViewedByMeDate(new Date())
                .build();

        return contents.commit(mGoogleApiClient, changeSet).await();
    }

    public Status commit(DriveContents contents, String title) {
        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                .setTitle(title)
                .setLastViewedByMeDate(new Date())
                .build();

        return contents.commit(mGoogleApiClient, changeSet).await();
    }

    public void discard(DriveContents contents) {
        contents.discard(mGoogleApiClient);
    }


    public static String openContents(DriveContents contents) {
        InputStream in;
        switch (contents.getMode()) {
            case DriveFile.MODE_READ_ONLY:
                in = contents.getInputStream();
                break;
            case DriveFile.MODE_READ_WRITE:
                in = new FileInputStream(contents.getParcelFileDescriptor().getFileDescriptor());
                break;

            default:
                return null;
        }

        try {
            return Base64.encodeToString(IOUtils.readContentFromStream(in), 0);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String openContents(DriveContents contents, String encoding) {
        InputStream in;
        switch (contents.getMode()) {
            case DriveFile.MODE_READ_ONLY:
                in = contents.getInputStream();
                break;
            case DriveFile.MODE_READ_WRITE:
                in = new FileInputStream(contents.getParcelFileDescriptor().getFileDescriptor());
                break;

            default:
                return null;
        }

        try {
            return IOUtils.readContentFromStream(in, encoding);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean writeContents(DriveContents contents, String byteBuffer) {
        OutputStream out;
        switch (contents.getMode()) {
            case DriveFile.MODE_WRITE_ONLY:
                out = contents.getOutputStream();
                break;
            case DriveFile.MODE_READ_WRITE:
                out = new FileOutputStream(contents.getParcelFileDescriptor().getFileDescriptor());
                break;

            default:
                return false;
        }

        try {
            IOUtils.writeContentToStream(out, byteBuffer);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    //*****


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
