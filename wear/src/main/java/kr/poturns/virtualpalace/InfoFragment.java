package kr.poturns.virtualpalace;

import android.os.Bundle;
import android.view.View;

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
    }
}
