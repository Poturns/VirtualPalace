package kr.poturns.virtualpalace;

import android.os.Bundle;
import android.view.View;

import kr.poturns.virtualpalace.input.OperationInputDetector;

/**
 * 특수한 명령을 전송하는 Fragment
 * <p/>
 * Created by Myungjin Kim on 2015-09-02.
 */
public class SpecialFragment extends BaseFragment {

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_special;
    }

    OperationInputDetector<Integer> detector = new OperationInputDetector<Integer>() {
        @Override
        public int isFocusingTo(Integer integer) {
            return super.isFocusingTo(integer);
        }

        @Override
        public int isGoingTo(Integer integer) {
            return super.isGoingTo(integer);
        }

        @Override
        public boolean isSelecting(Integer integer) {
            return super.isSelecting(integer);
        }

        @Override
        public int isTurningTo(Integer integer) {
            return super.isTurningTo(integer);
        }

        @Override
        public int isZoomingTo(Integer integer) {
            return super.isZoomingTo(integer);
        }

        public int detectSpecialCommand(Integer integer) {
            return OPERATION_NONE;
        }

    };

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }


}
