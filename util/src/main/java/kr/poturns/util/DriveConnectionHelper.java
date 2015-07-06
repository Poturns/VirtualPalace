package kr.poturns.util;


import android.content.Context;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
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

import java.io.FileDescriptor;

public class DriveConnectionHelper implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "DriveConnectionHelper";
    private GoogleApiClient mGoogleApiClient;
    private final Context context;

    public DriveConnectionHelper(Context context) {
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

    public final void connect() {
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }

    public final void disconnect() {
        if (mGoogleApiClient != null)
            mGoogleApiClient.disconnect();
    }

    public final void release() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
            mGoogleApiClient = null;
        }
    }

    public void createAppFolder() {
        Drive.DriveApi.newDriveContents(mGoogleApiClient)
                .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
                    @Override
                    public void onResult(DriveApi.DriveContentsResult driveContentsResult) {
                        if (!driveContentsResult.getStatus().isSuccess()) {
                            Log.e(TAG, "Error while trying to create new file contents");
                            return;
                        }

                        //TODO create file with right mineType
                        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                .setTitle("appconfig.txt")
                                .setMimeType("text/plain")
                                .build();

                        Drive.DriveApi.getAppFolder(mGoogleApiClient)
                                .createFile(mGoogleApiClient, changeSet, driveContentsResult.getDriveContents())
                                .setResultCallback(new ResultCallback<DriveFolder.DriveFileResult>() {
                                    @Override
                                    public void onResult(DriveFolder.DriveFileResult driveFileResult) {
                                        if (!driveFileResult.getStatus().isSuccess()) {
                                            Log.e(TAG, "Error while trying to create the file");
                                            return;
                                        }
                                        Toast.makeText(context, "Created a file in App Folder: " + driveFileResult.getDriveFile().getDriveId(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
    }

    public final void queryFiles() {
        //TODO set file filter
        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.MIME_TYPE, "text/plain"))
                .build();

        Drive.DriveApi.query(mGoogleApiClient, query)
                .setResultCallback(metadataCallback);
    }

    public final void queryFilesInAppFolder() {
        DriveFolder folder = Drive.DriveApi.getAppFolder(mGoogleApiClient);

        //TODO set file filter
        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.MIME_TYPE, "text/plain"))
                        //.addFilter(Filters.contains(SearchableField.TITLE, "a"))
                .build();

        folder.queryChildren(mGoogleApiClient, query)
                .setResultCallback(metadataCallback);
    }

    private final ResultCallback<DriveApi.MetadataBufferResult> metadataCallback = new ResultCallback<DriveApi.MetadataBufferResult>() {

        @Override
        public void onResult(DriveApi.MetadataBufferResult metadataBufferResult) {
            if (!metadataBufferResult.getStatus().isSuccess()) {
                Log.e(TAG, "Problem while retrieving results");
                return;
            }

            openFile(metadataBufferResult.getMetadataBuffer());
        }
    };

    private void openFile(MetadataBuffer metadataBuffer) {
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

            }
        }).setResultCallback(contentsOpenedCallback);
    }

    private final ResultCallback<DriveApi.DriveContentsResult> contentsOpenedCallback = new ResultCallback<DriveApi.DriveContentsResult>() {
        @Override
        public void onResult(DriveApi.DriveContentsResult result) {
            if (!result.getStatus().isSuccess()) {
                // display an error saying file can't be opened
                return;
            }

            DriveContents contents = result.getDriveContents();
            ParcelFileDescriptor parcelFileDescriptor = contents.getParcelFileDescriptor();
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();

            //TODO open file or send fileDescriptor information
        }
    };


    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int cause) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
