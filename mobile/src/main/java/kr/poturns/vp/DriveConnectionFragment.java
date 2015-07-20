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

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;

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
                dialog.show();
                textViewLog("\n+Create Dummy File in App Folder\n");
                mDriveConnectionHelper.createDummyFileInAppFolder(new ResultCallback<DriveFolder.DriveFileResult>() {
                    @Override
                    public void onResult(DriveFolder.DriveFileResult driveFileResult) {
                        dialog.dismiss();
                        if (!driveFileResult.getStatus().isSuccess()) {
                            textViewLog("+Error while trying to create the file\n");
                            return;
                        }
                        textViewLog("+Created a file in App Folder : " + driveFileResult.getDriveFile().getDriveId() + "\n");
                    }
                });
            }
        });

        v.findViewById(R.id.drive_query_dummy_file).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        textViewLog("\n+query Dummy File in App Folder\n");
                        dialog.show();
                        mDriveConnectionHelper.queryFileDummyFileInAppFolder(
                                new DriveConnectionHelper.OnFileResultListener() {
                                    @Override
                                    public void onReceiveFileContent(final DriveContents contents) {
                                        textViewLog("+Operation : query Dummy File in App Folder success\n");

                                        textViewLog("+trying to open Dummy File\n");
                                        AsyncTask.execute(new Runnable() {
                                            @Override
                                            public void run() {
                                                FileDescriptor fileDescriptor = contents.getParcelFileDescriptor().getFileDescriptor();
                                                FileInputStream fs = new FileInputStream(fileDescriptor);
                                                String content;
                                                try {
                                                    content = IOUtils.readContentFromStream(fs, "utf-8");
                                                } catch (final IOException e) {
                                                    getActivity().runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            textViewLog("+reading a dummy file has failed\ncause:" + e.toString() + "\n");
                                                            dialog.dismiss();
                                                        }
                                                    });
                                                    e.printStackTrace();
                                                    return;
                                                }

                                                final String fileContents = content;
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        textViewLog("+Dummy File Contents : \n----------- Contents ---------\n" + fileContents
                                                                + "\n------------------------------------\n");
                                                        textViewLog("+appending \'hello world\' to Dummy File\n");
                                                    }
                                                });

                                                try {
                                                    FileOutputStream fileOutputStream = new FileOutputStream(fileDescriptor);
                                                    Writer writer = new OutputStreamWriter(fileOutputStream);
                                                    writer.write("hello world\n");

                                                    writer.close();
                                                    fileOutputStream.close();
                                                    fs.close();

                                                    mDriveConnectionHelper.commitFile(contents, new ResultCallback<Status>() {
                                                        @Override
                                                        public void onResult(Status status) {
                                                            textViewLog("+appending success\n");
                                                            dialog.dismiss();
                                                        }
                                                    });
                                                } catch (final IOException e) {
                                                    getActivity().runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            textViewLog("+appending has failed\ncause:" + e.toString() + "\n");
                                                            dialog.dismiss();
                                                        }
                                                    });
                                                    e.printStackTrace();
                                                }
                                            }
                                        });

                                    }


                                    @Override
                                    public void onError(Status status) {
                                        dialog.dismiss();
                                        textViewLog("Error while trying to read a dummy file\n");
                                    }
                                }

                        );
                    }
                }

        );

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
}
