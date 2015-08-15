package kr.poturns.vp.media;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import kr.poturns.util.media.image.ImageDirInfo;
import kr.poturns.util.media.image.ImageInfo;
import kr.poturns.vp.R;

public class GalleryFragment extends Fragment {

    GridAdapter gridAdapter;
    ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("loading...");

        View v = inflater.inflate(R.layout.fragment_gallery, container, false);
        GridView gridView = (GridView) v.findViewById(R.id.gridview);
        gridView.setAdapter(gridAdapter = new GridAdapter(new ArrayList<ImageDirInfo>()));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                progressDialog.show();
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        final ArrayList<ImageInfo> list = new ArrayList<>();
                        list.addAll(gridAdapter.getItem(position).getInfoList(getActivity()));

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                Fragment fragment = new GalleryDetailFragment();

                                Bundle bundle = new Bundle();
                                bundle.putParcelableArrayList("list", list);
                                fragment.setArguments(bundle);

                                getActivity().getSupportFragmentManager().beginTransaction()
                                        .hide(GalleryFragment.this)
                                        .add(R.id.container_gallery, fragment)
                                        .addToBackStack(null)
                                        .commit();
                            }
                        });
                    }
                });

            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (gridAdapter.getCount() == 0) {
            progressDialog.show();
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    gridAdapter.addAll(ImageDirInfo.getDirList(getActivity()));

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            gridAdapter.notifyDataSetChanged();
                        }
                    });
                }
            });
        }
    }

    private static class GridAdapter extends BaseAdapter {
        private List<ImageDirInfo> list;

        public GridAdapter(List<ImageDirInfo> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        public void addAll(List<ImageDirInfo> list) {
            this.list.addAll(list);
        }

        @Override
        public ImageDirInfo getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vh;
            if (convertView == null) {
                vh = new ViewHolder(parent);
                convertView = vh.itemView;
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }
            vh.info = getItem(position);
            vh.setView();
            return convertView;
        }

        private static class ViewHolder {
            public final View itemView;
            public final ImageView imageView;
            public final TextView textView;
            public ImageDirInfo info;
            AsyncTask<Void, Void, Bitmap> task;

            public ViewHolder(ViewGroup parent) {
                this.itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_gallery, parent, false);
                imageView = (ImageView) itemView.findViewById(R.id.image);
                textView = (TextView) itemView.findViewById(R.id.text);
            }

            public void setView() {
                textView.setText(info.dirName);
                imageView.setImageBitmap(null);

                if (task != null && task.getStatus() != AsyncTask.Status.FINISHED) {
                    task.cancel(true);
                    task = null;
                }
                task = new AsyncTask<Void, Void, Bitmap>() {

                    @Override
                    protected Bitmap doInBackground(Void... params) {
                        return info.firstInfo.loadSuitableBitmap();
                    }

                    @Override
                    protected void onPostExecute(Bitmap bitmap) {
                        imageView.setImageBitmap(bitmap);
                    }

                    @Override
                    protected void onCancelled(Bitmap bitmap) {
                        if (bitmap != null)
                            bitmap.recycle();
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            }
        }

    }


}
