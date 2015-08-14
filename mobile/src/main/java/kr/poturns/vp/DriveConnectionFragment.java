package kr.poturns.vp;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.MetadataBuffer;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import kr.poturns.util.DriveConnectionHelper;
import kr.poturns.util.IOUtils;

/*
    문제점 1. 초기 사용시 사용자가 Google Drive 사용에 관한 dialog를 보고, 이를 수락해야 함.

 */
public class DriveConnectionFragment extends Fragment {
    private DriveConnectionHelper mDriveConnectionHelper;

    ProgressDialog dialog;

    TextView textView;
    StringBuilder sb = new StringBuilder();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("loading");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);

        mDriveConnectionHelper = new DriveConnectionHelper(getActivity());

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_drive, container, false);

        textView = (TextView) v.findViewById(R.id.log);

        v.findViewById(R.id.drive_create_dummy_in_appfolder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processCreateDummyFileInAppFolder();
            }
        });

        v.findViewById(R.id.drive_query_dummy_file).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processQueryFileDummyFileInAppFolder();
            }
        });

        return v;
    }

    void textViewLog(String s) {
        sb.append(s);
        textView.setText(sb.toString());
    }

    @Override
    public void onResume() {
        super.onResume();

        mDriveConnectionHelper.resume();
    }

    @Override
    public void onPause() {
        super.onPause();

        mDriveConnectionHelper.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mDriveConnectionHelper.destroy();
    }


    private DriveFile createDummyFileInAppFolder() {
        return mDriveConnectionHelper.createFile(mDriveConnectionHelper.getAppFolder(), "appconfig.txt", "text/plain", mDriveConnectionHelper.newDriveContents());
    }

    private MetadataBuffer queryFileDummyFileInAppFolder() {
        return mDriveConnectionHelper.queryChildrenByTitle(mDriveConnectionHelper.getAppFolder(), "appconfig.txt");
    }

    private void processCreateDummyFileInAppFolder() {
        dialog.show();
        textViewLog("\n+Create Dummy File in App Folder\n");

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                final DriveFile file = createDummyFileInAppFolder();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        if (file == null) {
                            textViewLog("+Error while trying to create the file\n");
                        } else {
                            textViewLog("+Created a file in App Folder : " + file.getDriveId() + "\n");

                        }
                    }
                });
            }
        });
    }

    private void processQueryFileDummyFileInAppFolder() {
        textViewLog("\n+query Dummy File in App Folder\n");
        dialog.show();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                MetadataBuffer metadataBuffer = queryFileDummyFileInAppFolder();

                if (metadataBuffer == null) {
                    logOnUiThread("Error while trying to read a dummy file\n", true);
                    return;
                }


                final DriveContents contents = mDriveConnectionHelper.openFile(metadataBuffer.get(0).getDriveId(), DriveFile.MODE_READ_WRITE);

                logOnUiThread("+Operation : query Dummy File in App Folder success\n"
                        + "+trying to open Dummy File\n", false);

                if (contents == null) {
                    metadataBuffer.release();
                    logOnUiThread("Error while trying to read a dummy file\n", true);
                    return;
                }

                FileDescriptor fileDescriptor = contents.getParcelFileDescriptor().getFileDescriptor();
                FileInputStream fs = new FileInputStream(fileDescriptor);
                String content;
                try {
                    content = IOUtils.readContentFromStream(fs, "utf-8");
                } catch (final IOException e) {
                    logOnUiThread("+reading a dummy file has failed\ncause:" + e.toString() + "\n", true);
                    e.printStackTrace();
                    return;
                }

                logOnUiThread(
                        "+Dummy File Contents : \n----------- Contents ---------\n" + content
                                + "\n------------------------------------\n" +
                                "+appending \'hello world\' to Dummy File\n"
                        , false);

                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(fileDescriptor);
                    Writer writer = new OutputStreamWriter(fileOutputStream);
                    writer.write("hello world\n");

                    writer.close();
                    fileOutputStream.close();
                    fs.close();

                    if (mDriveConnectionHelper.commit(contents).isSuccess()) {
                        logOnUiThread("+appending success\n", true);
                    } else {
                        logOnUiThread("+appending has failed\n", true);
                    }

                } catch (final IOException e) {
                    logOnUiThread("+appending has failed\ncause:" + e.toString() + "\n", true);
                    e.printStackTrace();
                } finally {
                    metadataBuffer.release();
                }
            }
        });

    }

    private void logOnUiThread(final String msg, final boolean dismissDialog) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textViewLog(msg);
                if (dismissDialog) dialog.dismiss();
            }
        });
    }

}
