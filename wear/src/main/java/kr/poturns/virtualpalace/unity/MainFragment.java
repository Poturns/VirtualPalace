package kr.poturns.virtualpalace.unity;

import android.app.Fragment;
import android.databinding.BindingAdapter;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.wearable.view.CircledImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;
import java.util.List;

import kr.poturns.virtualpalace.unity.databinding.ListMainItemBinding;

/**
 * 웨어러블의 주 화면을 구성하는 Fragment
 * <p/>
 * Created by Myungjin Kim on 2015-09-01.
 */
public class MainFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        viewPager.setAdapter(new ViewPagerAdapter());
    }

    private class ViewPagerAdapter extends PagerAdapter {

        private final List<ButtonIcon> buttonIcons;

        public ViewPagerAdapter() {
            ButtonIcon[] values = ButtonIcon.values();
            for (final ButtonIcon icon : values) {

                icon.setClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (icon.fname == null) {
                            getActivity().finish();
                        } else {
                            getFragmentManager().beginTransaction()
                                    .hide(MainFragment.this)
                                    .add(R.id.content, Fragment.instantiate(getActivity(), icon.fname))
                                    .addToBackStack("back")
                                    .commit();
                        }

                    }
                });
            }
            buttonIcons = Arrays.asList(values);
        }

        @Override
        public int getCount() {
            return buttonIcons.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return object instanceof ListMainItemBinding && ((ListMainItemBinding) object).root.equals(view);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ButtonIcon buttonIcon = buttonIcons.get(position);
            ListMainItemBinding binding = ListMainItemBinding.inflate(LayoutInflater.from(container.getContext()), container, false);
            binding.setItem(buttonIcon);

            container.addView(binding.root);

            return binding;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if (object instanceof ListMainItemBinding)
                container.removeView(((ListMainItemBinding) object).root);
        }

    }

    /**
     * ViewPager에서 표시되는 버튼과 버튼 라벨, 색, 버튼을 눌렀을 때 수행하는 행동에 대한 것이 기술되어있다.
     */
    @SuppressWarnings({"unused", "deprecation"})
    public enum ButtonIcon {
        /**
         * 일반 입력 모드
         */
        NORMAL_INPUT(
                R.drawable.ic_gesture,
                R.color.material_yellow_600,
                R.string.icon_text_normal_input,
                BasicInputFragment.class.getName()
        ),

        DIRECTION_INPUT(
                R.drawable.ic_arrow_up,
                R.color.material_green_600,
                R.string.icon_text_direction_input,
                DirectionInputFragment.class.getName()
        ),
        /*
        /**
         * 특수 입력 모드
         *
        SPECIAL_INPUT(
                R.drawable.ic_action_toggle_star,
                R.color.material_indigo_700,
                R.string.icon_text_special_input,
                SpecialFragment.class.getName()
        ),

        */
        /*
        //터치 스트리밍 모드
        TOUCH_STREAMING(
                R.drawable.ic_action_content_gesture,
                R.color.material_light_blue_700,
                R.string.icon_text_streaming_input,
                TouchGestureFragment.class.getName()
        ),
        */

        /**
         * 기타
         */
        ETC(
                R.drawable.ic_action_action_info,
                R.color.material_blue_600,
                R.string.icon_text_etc,
                InfoFragment.class.getName()
        ),

        /**
         * 종료
         */
        EXIT(
                R.drawable.ic_action_action_exit_to_app,
                R.color.material_red_600,
                R.string.icon_text_exit,
                null
        );

        /**
         * 버튼 아이콘
         */
        private final int drawableRes;
        /**
         * 버튼 색
         */
        private final int colorRes;
        /**
         * 버튼 라벨
         */
        private final int textRes;
        /**
         * 버튼을 눌렀을 때, 실행될 Fragment, 없다면 Fragment를 보여주는 대신 앱을 종료한다.
         */
        private final String fname;
        /**
         * 버튼 클릭 리스너
         */
        private View.OnClickListener l;

        ButtonIcon(@DrawableRes int drawableRes, @ColorRes int colorRes, @StringRes int textRes, String fname) {
            this.drawableRes = drawableRes;
            this.colorRes = colorRes;
            this.textRes = textRes;
            this.fname = fname;
        }

        /**
         * 버튼 아이콘 리소스를 반환한다.
         */
        public int getDrawableRes() {
            return drawableRes;
        }

        /**
         * 버튼 색깔 리소스를 반환한다.
         */
        public int getColorRes() {
            return colorRes;
        }

        /**
         * 버튼 라벨 리소스를 반환한다.
         */
        public int getTextRes() {
            return textRes;
        }

        /**
         * 버튼 이미지를 설정한다.
         */
        @BindingAdapter("android:src")
        public static void setSrc(CircledImageView view, @DrawableRes int res) {
            view.setImageResource(res);
        }

        /**
         * 버튼 색을 설정한다.
         */
        @BindingAdapter("bind:circle_color")
        public static void setCircleColor(CircledImageView view, @ColorRes int res) {
            view.setCircleColor(view.getContext().getResources().getColor(res));
        }

        /**
         * 버튼이 클릭되었을 때 호출된다.
         */
        public void onClickImage(View view) {
            if (l != null)
                l.onClick(view);
        }

        /**
         * 버튼 클릭 리스너를 설정한다.
         */
        public void setClickListener(View.OnClickListener l) {
            this.l = l;
        }

    }
}
