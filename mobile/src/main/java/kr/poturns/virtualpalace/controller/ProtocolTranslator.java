package kr.poturns.virtualpalace.controller;

import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

import kr.poturns.virtualpalace.input.IOperationInputFilter;

/**
 * Created by YeonhoKim on 2015-10-02.
 */
public class ProtocolTranslator {


    public List<Pair<Integer, Integer>> parseDirectionAmount(int value) {
        ArrayList<Pair<Integer, Integer>> list = new ArrayList<Pair<Integer, Integer>>();


        int direction = value / IOperationInputFilter.Direction.SEPARATION;
        int amount = value % IOperationInputFilter.Direction.SEPARATION;
        if (amount % 10 == 0) {
            // 3차원 축 계산
            if (direction > 100) {
                int d_3d = (direction > 500)? IOperationInputFilter.Direction.UPWARD :
                        (direction < 500)? IOperationInputFilter.Direction.DOWNWARD : 0;
                int a_3d = amount / (10 * 100);

                if (d_3d > 0)
                    list.add(new Pair<Integer, Integer>(d_3d, a_3d));

                direction %=  100;
                amount %= (10 * 100);
            }

            // 2차원 축 계산
            if (direction> 10) {
                int d_2d = (direction > 50)? IOperationInputFilter.Direction.UP :
                        (direction < 50)? IOperationInputFilter.Direction.DOWN : 0;
                int a_2d = amount / (10 * 10);

                if (d_2d > 0)
                    list.add(new Pair<Integer, Integer>(d_2d, a_2d));

                direction %=  10;
                amount %= (10 * 10);
            }

            // 1차원 축 계산
            int d_1d = (direction > 5)? IOperationInputFilter.Direction.RIGHT :
                    (direction < 5)? IOperationInputFilter.Direction.LEFT : 0;
            int a_1d = amount / (10 * 1);

            if (d_1d > 0)
                list.add(new Pair<Integer, Integer>(d_1d, a_1d));

        } else
            list.add(new Pair<Integer, Integer>(direction, amount / 10));


        return list;
    }

}
