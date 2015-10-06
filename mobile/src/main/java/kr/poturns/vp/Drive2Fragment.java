package kr.poturns.vp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.ParentReference;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import kr.poturns.util.DriveWebClient;
import kr.poturns.util.IOUtils;

/**
 * Created by Myungjin Kim on 2015-10-06.
 */
public class Drive2Fragment extends Fragment implements MainActivity.OnBackPressListener {

    private DriveWebClient driveWebClient;
    private Adapter adapter;
    private ProgressDialog mProgressDialog;
    private String mCurrentFolderId;
    private AlertDialog alertDialog;
    private ParentReference parentReference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);

        alertDialog = new AlertDialog.Builder(getActivity())
                .setNegativeButton(android.R.string.no, null)
                .create();

        adapter = new Adapter(getActivity());
        adapter.setNotifyOnChange(false);
        driveWebClient = new DriveWebClient(getActivity());
        initClient();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_drive2, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ListView listView = (ListView) view.findViewById(R.id.listview);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final File file = adapter.getItem(position);

                if (DriveWebClient.isFolder(file))
                    requestChildren(file.getId());
                else {
                    alertDialog.setMessage("[" + file.getTitle() + "] 열기 ?");
                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (file.getDownloadUrl() != null && file.getDownloadUrl().length() > 0)
                                requestOpenFile(file);
                            else if (file.getSelfLink() != null)
                                openFileURL(file);
                            else
                                Toast.makeText(getActivity(), "파일 열기 실패", Toast.LENGTH_SHORT).show();

                        }
                    });
                    alertDialog.show();
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        driveWebClient.destroy();
    }

    private void initClient() {
        requestChildren(DriveWebClient.FOLDER_ID_ROOT);
    }

    private void requestChildren(final String fileId) {
        mProgressDialog.show();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final List<File> list = driveWebClient.requestChildFileList(fileId);
                    mCurrentFolderId = fileId;

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.clear();
                            adapter.addAll(list);
                            adapter.notifyDataSetChanged();
                        }
                    });
                } catch (UserRecoverableAuthIOException e) {
                    handlingAuthException(e);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressDialog.dismiss();
                    }
                });
            }
        });
    }

    private void requestParent(final String fileId) {
        mProgressDialog.show();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final List<ParentReference> parentReferenceList = driveWebClient.requestParentReferenceList(fileId);
                    final List<File> parentList = driveWebClient.requestParentList(parentReferenceList);
                    String folderId = parentList.get(0).getId();
                    final List<File> list = driveWebClient.requestChildFileList(folderId);
                    mCurrentFolderId = folderId;
                    parentReference = parentReferenceList.get(0);

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.clear();
                            adapter.addAll(list);
                            adapter.notifyDataSetChanged();
                        }
                    });
                } catch (UserRecoverableAuthIOException e) {
                    handlingAuthException(e);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressDialog.dismiss();
                    }
                });
            }
        });
    }

    void openFileURL(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(file.getSelfLink()));

        startActivity(intent);
    }

    void requestOpenFile(final File file) {
        mProgressDialog.show();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final java.io.File downloadFile = new java.io.File(getActivity().getExternalCacheDir(), file.getTitle());
                    InputStream in = driveWebClient.openFile(file);

                    if (in == null)
                        throw new IOException("downloadUrl == null");

                    IOUtils.writeStreamToFile(in, downloadFile);

                    Uri fileUri = Uri.fromFile(downloadFile);
                    String fileExtension = MimeTypeMap.getFileExtensionFromUrl(fileUri.toString());
                    final String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension);

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgressDialog.dismiss();
                            try {
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_VIEW);
                                intent.setDataAndType(Uri.parse("file://" + downloadFile.getAbsolutePath()), mimeType);

                                startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(getActivity(), "파일 열기 실패", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                } catch (UserRecoverableAuthIOException e) {
                    handlingAuthException(e);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgressDialog.dismiss();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgressDialog.dismiss();
                        }
                    });
                }
            }
        });
    }

    private void handlingAuthException(final UserRecoverableAuthIOException e) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                requestAuth(e.getIntent());
            }
        });
    }

    protected void requestAuth(Intent intent) {
        startActivityForResult(intent, 1234);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 1234:
                if (resultCode == Activity.RESULT_OK)
                    initClient();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onBackPressed() {
        if (DriveWebClient.FOLDER_ID_ROOT.equals(mCurrentFolderId) || (parentReference != null && parentReference.getIsRoot()))
            return false;

        requestParent(mCurrentFolderId);

        return true;
    }

    private class Adapter extends ArrayAdapter<File> {

        public Adapter(Context context) {
            super(context, android.R.layout.simple_list_item_1);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
                textView = (TextView) convertView.findViewById(android.R.id.text1);
                convertView.setTag(textView);
            } else {
                textView = (TextView) convertView.getTag();
            }

            File file = getItem(position);
            textView.setText(file.getTitle());
            textView.setCompoundDrawablesWithIntrinsicBounds(DriveWebClient.isFolder(file) ? R.drawable.ic_action_file_folder : R.drawable.ic_action_editor_insert_drive_file, 0, 0, 0);

            return convertView;
        }
    }
}
