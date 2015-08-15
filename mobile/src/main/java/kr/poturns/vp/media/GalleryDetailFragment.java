package kr.poturns.vp.media;

import android.app.AlertDialog;
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

import kr.poturns.util.media.image.ImageInfo;
import kr.poturns.vp.R;

public class GalleryDetailFragment extends Fragment {
    AlertDialog alertDialog;
    ImageView imageView;
    GridAdapter gridAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View dialogView = inflater.inflate(R.layout.list_gallery, container, false);
        imageView = (ImageView) dialogView.findViewById(R.id.image);

        alertDialog = new AlertDialog.Builder(getActivity())
                .setView(dialogView)
                .create();
        View v = inflater.inflate(R.layout.fragment_gallery, container, false);
        GridView gridView = (GridView) v.findViewById(R.id.gridview);

        ArrayList<ImageInfo> list = getArguments().getParcelableArrayList("list");
        gridView.setAdapter(gridAdapter = new GridAdapter(list));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {

                        final Bitmap bitmap = gridAdapter.getItem(position).loadSuitableBitmap();

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setImageBitmap(bitmap);
                            }
                        });
                    }
                });


                alertDialog.show();
            }
        });
        return v;
    }


    private static class GridAdapter extends BaseAdapter {
        private List<ImageInfo> list;

        public GridAdapter(List<ImageInfo> list) {
            this.list = list;

            if (list == null)
                this.list = new ArrayList<>();
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public ImageInfo getItem(int position) {
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
            public ImageInfo info;
            AsyncTask<Void, Void, Bitmap> task;

            public ViewHolder(ViewGroup parent) {
                this.itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_gallery, parent, false);
                imageView = (ImageView) itemView.findViewById(R.id.image);
                textView = (TextView) itemView.findViewById(R.id.text);
            }

            public void setView() {
                textView.setText(info.displayName);
                imageView.setImageBitmap(null);

                if (task != null && task.getStatus() != AsyncTask.Status.FINISHED) {
                    task.cancel(true);
                    task = null;
                }
                task = new AsyncTask<Void, Void, Bitmap>() {

                    @Override
                    protected Bitmap doInBackground(Void... params) {
                        return info.loadSuitableBitmap();
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
