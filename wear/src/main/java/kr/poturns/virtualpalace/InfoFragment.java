package kr.poturns.virtualpalace;

import android.os.Bundle;
import android.support.wearable.view.WearableListView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 기타 Fragment
 * Created by Myungjin Kim on 2015-09-02.
 */
public class InfoFragment extends BaseFragment {

    static final int[] LIST_ICONS = {
            R.drawable.gesture_up,
            R.drawable.gesture_down,
            R.drawable.gesture_left,
            R.drawable.gesture_right,
            R.drawable.gesture_cancel1,
            R.drawable.gesture_cancel2,
            R.drawable.gesture_deep,
            R.drawable.gesture_menu,
            R.drawable.gesture_select,
            R.drawable.gesture_switch_mode
    };

    static final String[] LIST_GESTURE_NAME = {
            "Up",
            "Down",
            "Left",
            "Right",
            "Cancel",
            "Cancel",
            "Deep",
            "Menu",
            "Select",
            "Switch Mode"
    };

    static final String[] LIST_GESTURE_DESCRIPTION = {
            "위쪽으로 향하라는 명령을 보낸다.",
            "아래쪽으로 향하라는 명령을 보낸다.",
            "왼쪽으로 향하라는 명령을 보낸다.",
            "오른쪽으로 향하라는 명령을 보낸다.",
            "취소 명령을 보낸다.",
            "취소 명령을 보낸다.",
            "특수한 명령을 보낸다.",
            "메뉴화면을 호출한다.",
            "선택 명령을 보낸다.",
            "모드 전환 명령을 보낸다. "
    };

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_info;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        WearableListView listView = (WearableListView) view.findViewById(android.R.id.list);
        listView.setAdapter(new WearableListView.Adapter() {
            @Override
            public WearableListView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_info_item, parent, false));
            }

            @Override
            public void onBindViewHolder(WearableListView.ViewHolder holder, int position) {
                ((ViewHolder) holder).setView(position);
            }

            @Override
            public int getItemCount() {
                return LIST_ICONS.length;
            }

            class ViewHolder extends WearableListView.ViewHolder {
                public final ImageView imageView;
                public final TextView title;

                public ViewHolder(View itemView) {
                    super(itemView);
                    imageView = (ImageView) itemView.findViewById(R.id.image);
                    title = (TextView) itemView.findViewById(R.id.title);
                }

                public void setView(int pos) {
                    imageView.setImageResource(LIST_ICONS[pos]);
                    title.setText(LIST_GESTURE_NAME[pos]);
                }
            }
        });
    }
}
