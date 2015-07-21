package kr.poturns.util;


import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

public class DriveConnectionHelper extends InputHandleHelper.ContextInputHandleHelper implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "DriveConnectionHelper";
    private GoogleApiClient mGoogleApiClient;

    public interface OnFileResultListener {
        void onReceiveFileContent(DriveContents contents);

        void onError(Status status);

    }

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

    @Override
    public final void resume() {
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }

    @Override
    public final void pause() {
        if (mGoogleApiClient != null)
            mGoogleApiClient.disconnect();
    }

    @Override
    public final void destroy() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
            mGoogleApiClient = null;
        }
    }

    public void createDummyFileInAppFolder(final ResultCallback<DriveFolder.DriveFileResult> callback) {
        Drive.DriveApi.newDriveContents(mGoogleApiClient)
                .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
                    @Override
                    public void onResult(DriveApi.DriveContentsResult driveContentsResult) {
                        if (!driveContentsResult.getStatus().isSuccess()) {
                            Log.e(TAG, "Error while trying to create new file contents");
                            return;
                        }

                        createDummyFile(driveContentsResult, callback);
                    }
                });
    }

    void createDummyFile(DriveApi.DriveContentsResult driveContentsResult, ResultCallback<DriveFolder.DriveFileResult> callback) {
        //TODO create file with right mineType
        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                .setTitle("appconfig.txt")
                .setMimeType("text/plain")
                .build();

        Drive.DriveApi.getAppFolder(mGoogleApiClient)
                .createFile(mGoogleApiClient, changeSet, driveContentsResult.getDriveContents())
                .setResultCallback(callback);
    }

    public final void queryFiles(final OnFileResultListener onFileResultListener) {
        //TODO set file filter
        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.MIME_TYPE, "text/plain"))
                .build();

        Drive.DriveApi.query(mGoogleApiClient, query)
                .setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {

                    @Override
                    public void onResult(DriveApi.MetadataBufferResult metadataBufferResult) {
                        if (!metadataBufferResult.getStatus().isSuccess()) {
                            onFileResultListener.onError(metadataBufferResult.getStatus());
                            Log.e(TAG, "Problem while retrieving results");
                            return;
                        }

                        openFile(metadataBufferResult.getMetadataBuffer(), onFileResultListener);
                    }
                });
    }

    public final void queryFileDummyFileInAppFolder(final OnFileResultListener onFileResultListener) {
        DriveFolder folder = Drive.DriveApi.getAppFolder(mGoogleApiClient);

        //TODO set file filter
        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.MIME_TYPE, "text/plain"))
                        //.addFilter(Filters.contains(SearchableField.TITLE, "a"))
                .build();

        folder.queryChildren(mGoogleApiClient, query)
                .setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {

                    @Override
                    public void onResult(DriveApi.MetadataBufferResult metadataBufferResult) {
                        if (!metadataBufferResult.getStatus().isSuccess()) {
                            onFileResultListener.onError(metadataBufferResult.getStatus());
                            Log.e(TAG, "Problem while retrieving results");
                            return;
                        }

                        openFile(metadataBufferResult.getMetadataBuffer(), onFileResultListener);
                    }
                });
    }


    private void openFile(MetadataBuffer metadataBuffer, final OnFileResultListener onFileResultListener) {
        DriveId id = null;
        // TODO get file ID
        for (Metadata metadata : metadataBuffer) {
            id = metadata.getDriveId();
            if (id != null)
                break;
        }
        metadataBuffer.release();

        DriveFile driveFile = Drive.DriveApi.getFile(mGoogleApiClient, id);
        driveFile.open(mGoogleApiClient, DriveFile.MODE_READ_WRITE, new DriveFile.DownloadProgressListener() {
            @Override
            public void onProgress(long bytesDownloaded, long bytesExpected) {
                Log.d(TAG, bytesDownloaded + " / " + bytesExpected);
            }
        }).setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
            @Override
            public void onResult(DriveApi.DriveContentsResult result) {
                if (!result.getStatus().isSuccess()) {
                    // display an error saying file can't be opened
                    onFileResultListener.onError(result.getStatus());
                    return;
                }

                //TODO open file or send fileDescriptor information
                DriveContents contents = result.getDriveContents();
                onFileResultListener.onReceiveFileContent(contents);


            }
        });
    }


    public final void commitFile(DriveContents driveContents, ResultCallback<Status> resultCallback) {
        driveContents.commit(mGoogleApiClient, null).setResultCallback(resultCallback);
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



}
