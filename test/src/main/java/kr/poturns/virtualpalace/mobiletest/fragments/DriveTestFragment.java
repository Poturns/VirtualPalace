package kr.poturns.virtualpalace.mobiletest.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.widget.DataBufferAdapter;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import kr.poturns.virtualpalace.controller.PalaceApplication;
import kr.poturns.virtualpalace.controller.PalaceMaster;
import kr.poturns.virtualpalace.mobiletest.R;
import kr.poturns.virtualpalace.util.DriveAssistant;
import kr.poturns.virtualpalace.util.IOUtils;

/**
 * Created by Myungjin Kim on 2015-10-30.
 *
 * DriveAssistant test Fragment
 */
public class DriveTestFragment extends Fragment implements View.OnClickListener{
    private DriveAssistant mDriveAssistant;

    ProgressDialog dialog;
    AlertDialog alertDialog;
    TextView textView;

    StringBuilder sb = new StringBuilder();
    MetadataAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("loading");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);

        textView = new TextView(getActivity());

        alertDialog = new AlertDialog.Builder(getActivity())
                .setView(textView)
                .create();

        mDriveAssistant = new DriveAssistant(getActivity());
        mDriveAssistant.connect();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_drive, container, false);
        ListView listView = (ListView) v.findViewById(R.id.listview);
        listView.setAdapter(adapter = new MetadataAdapter(getActivity()));
        adapter.setNotifyOnChange(false);

        v.findViewById(R.id.button).setOnClickListener(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Metadata metadata = adapter.getItem(position);
                if (metadata.isFolder()) {
                    openFolder(metadata);
                } else {

                    if (metadata.getMimeType().startsWith("text")) {
                        openTextContents(metadata);
                    }

                }

            }
        });
        initRoot();

        return v;
    }

    void textViewLog(String s) {
        sb.append(s);
        //textView.setText(sb.toString());
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!mDriveAssistant.isConnected()) {
            mDriveAssistant.connect();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mDriveAssistant.isConnected())
            mDriveAssistant.disconnect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mDriveAssistant.destroy();
    }


    private void openTextContents(final Metadata metadata) {
        dialog.show();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                final String s = DriveAssistant.IDriveContentsApi.openStringContents(mDriveAssistant.openFile(metadata.getDriveId(), DriveFile.MODE_READ_ONLY), "utf-8");

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(s);
                        alertDialog.show();
                    }
                });
            }
        });
    }

    private void openFolder(final Metadata metadata) {
        if (!metadata.isFolder()) {
            Toast.makeText(getActivity(), "폴더가 아닙니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        dialog.show();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                MetadataBuffer metadataBuffer = mDriveAssistant.DriveFolderApi.listChildren(mDriveAssistant.getFolder(metadata.getDriveId()));
                adapter.clear();
                adapter.append(metadataBuffer);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    private void initRoot() {
        dialog.show();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                while (!mDriveAssistant.isConnected()) {
                    synchronized (this) {
                        try {
                            wait(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                MetadataBuffer metadataBuffer = mDriveAssistant.DriveFolderApi.listChildren(mDriveAssistant.getAppFolder());
                adapter.append(metadataBuffer);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.button:
                PalaceApplication app = (PalaceApplication) getActivity().getApplication();
                PalaceMaster master = PalaceMaster.getInstance(app);
                master.testDrive(mDriveAssistant);

                break;
        }
    }

    private static class MetadataAdapter extends DataBufferAdapter<Metadata> {
        LayoutInflater inflater;

        public MetadataAdapter(Context context) {
            super(context, R.layout.list_drive_meta);
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView type, name;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.list_drive_meta, parent, false);
                type = (TextView) convertView.findViewById(R.id.drive_meta_type);
                name = (TextView) convertView.findViewById(R.id.drive_meta_name);
                convertView.setTag(R.id.drive_meta_type, type);
                convertView.setTag(R.id.drive_meta_name, name);
            } else {
                type = (TextView) convertView.getTag(R.id.drive_meta_type);
                name = (TextView) convertView.getTag(R.id.drive_meta_name);
            }
            Metadata metadata = getItem(position);
            type.setText(metadata.getMimeType());
            name.setText(metadata.getTitle());
            return convertView;
        }
    }


    private DriveFile createDummyFileInAppFolder() {
        return mDriveAssistant.DriveFolderApi.createFile(mDriveAssistant.getAppFolder(), mDriveAssistant.newDriveContents(), "appconfig.txt", "text/plain");
    }

    private MetadataBuffer queryFileDummyFileInAppFolder() {
        return mDriveAssistant.DriveFolderApi.queryChildrenByTitle(mDriveAssistant.getAppFolder(), "appconfig.txt");
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


                final DriveContents contents = mDriveAssistant.openFile(metadataBuffer.get(0).getDriveId(), DriveFile.MODE_READ_WRITE);

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
                    content = IOUtils.readStringContent(fs, "utf-8");
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

                    if (mDriveAssistant.DriveContentsApi.commit(contents).isSuccess()) {
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
