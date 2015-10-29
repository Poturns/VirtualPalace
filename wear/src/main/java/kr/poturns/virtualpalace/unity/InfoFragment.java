package kr.poturns.virtualpalace.unity;

import android.databinding.BindingAdapter;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.wearable.view.WearableListView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import kr.poturns.virtualpalace.unity.databinding.ListInfoItemBinding;

/**
 * 기타 Fragment
 * Created by Myungjin Kim on 2015-09-02.
 */
public class InfoFragment extends BaseFragment {


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
            private InfoItem[] items = InfoItem.values();

            @Override
            public WearableListView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new ViewHolder(ListInfoItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
            }

            @Override
            public void onBindViewHolder(WearableListView.ViewHolder holder, int position) {
                ((ViewHolder) holder).binding.setItem(items[position]);
            }

            @Override
            public int getItemCount() {
                return items.length;
            }

            class ViewHolder extends WearableListView.ViewHolder {
                public final ListInfoItemBinding binding;

                public ViewHolder(ListInfoItemBinding binding) {
                    super(binding.getRoot());
                    this.binding = binding;
                }
            }
        });
    }

    @SuppressWarnings("unused")
    public enum InfoItem {
        UP, DOWN, LEFT, RIGHT, CANCEL1, CANCEL2, DEEP, MENU, SELECT, SWITCH_MODE;

        public final int getIcon() {
            return LIST_ICONS[ordinal()];
        }

        public final String getName() {
            return LIST_GESTURE_NAME[ordinal()];
        }

        public final String getDescription() {
            return LIST_GESTURE_DESCRIPTION[ordinal()];
        }

        @BindingAdapter("android:src")
        public static void setSrc(ImageView view, @DrawableRes int res) {
            view.setImageResource(res);
        }
    }

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

}
