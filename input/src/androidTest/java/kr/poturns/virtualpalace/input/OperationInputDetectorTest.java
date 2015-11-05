package kr.poturns.virtualpalace.input;

import android.test.AndroidTestCase;
import android.util.Log;

import java.util.Random;

/**
 * Created by YeonhoKim on 2015-09-05.
 */
public class OperationInputDetectorTest extends AndroidTestCase {

    public void testGoDetect() {
        mDetector.setOperationInputConnector(mConnector);
        assertEquals(IOperationInputFilter.Direction.RIGHT, mDetector.isGoingTo("GO_RIGHT"));
        assertEquals(true, mDetector.detect("GO_LEFT"));
    }

    public void testTurnDetect() {
        mDetector.setOperationInputConnector(mConnector);
        assertEquals(IOperationInputFilter.Direction.UP, mDetector.isTurningTo("TURN_UP"));
        assertEquals(true, mDetector.detect("TURN_DOWN"));
    }

    public void testFocusDetect() {
        mDetector.setOperationInputConnector(mConnector);
        assertEquals(IOperationInputFilter.Direction.LEFT, mDetector.isFocusingTo("FOCUS_LEFT"));
        assertEquals(true, mDetector.detect("FOCUS_RIGHT"));
    }

    public void testZoomDetect() {
        mDetector.setOperationInputConnector(mConnector);
        assertEquals(IOperationInputFilter.Direction.DOWN, mDetector.isZoomingTo("ZOOM_DOWN"));
        assertEquals(true, mDetector.detect("ZOOM_UP"));
    }

    public void testSelectCancelDetect() {
        mDetector.setOperationInputConnector(mConnector);
        assertEquals(true, mDetector.isSelecting("SELECT"));
        assertEquals(true, mDetector.isCanceling("CANCEL"));
        assertEquals(true, mDetector.detect("SELECT"));
    }

    public void testKeyDetect() {
        mDetector.setOperationInputConnector(mConnector);
        assertEquals(IOperationInputFilter.Operation.KEY_VOLUME_DOWN, mDetector.isKeyPressed("VOLUME_DOWN"));
    }

    public void testSpecialDetect() {
        mDetector.setOperationInputConnector(mConnector);
        assertEquals(IOperationInputFilter.Operation.TERMINATE, mDetector.isSpecialOperation("EXIT"));
    }

    public void testBatchDetect() {
        mDetector.setOperationInputConnector(mConnector);

        String[] random_operation = {
                "GO_LEFT",
                "TURN_RIGHT",
                "FOCUS_UP",
                "ZOOM_DOWN",
                "SELECT",
                "CANCEL",
                "HOME",
                "SWITCH"
        };

        // 동일 명령 연속 발생 테스트
        int success = 0;
        for(int i=0; i<30; i++) {
            if (mDetector.detect("FOCUS_LEFT"))
                success++;
        }
        assertEquals(30, success);

        // 랜덤 명령 연속 발생 테스트
        success = 0;
        Random random = new Random(System.currentTimeMillis());
        for(int i=0; i<30; i++) {
             if (mDetector.detect(random_operation[ Math.abs(random.nextInt()) % random_operation.length ]))
                 success++;
        }
        assertEquals(30, success);
    }


    OperationInputDetector<String> mDetector = new OperationInputDetector<String>(new IOperationInputFilter<String>() {
        @Override
        public int isGoingTo(String s) {
            String cap = s.toUpperCase();

            if ("GO_UP".equals(cap))
                return Direction.UP;
            else if ("GO_DOWN".equals(cap))
                return Direction.DOWN;
            else if ("GO_LEFT".equals(cap))
                return Direction.LEFT;
            else if ("GO_RIGHT".equals(cap))
                return Direction.RIGHT;

            return Direction.NONE;
        }

        @Override
        public int isTurningTo(String s) {
            String cap = s.toUpperCase();

            if ("TURN_UP".equals(cap))
                return Direction.UP;
            else if ("TURN_DOWN".equals(cap))
                return Direction.DOWN;
            else if ("TURN_LEFT".equals(cap))
                return Direction.LEFT;
            else if ("TURN_RIGHT".equals(cap))
                return Direction.RIGHT;

            return Direction.NONE;
        }

        @Override
        public int isFocusingTo(String s) {
            String cap = s.toUpperCase();

            if ("FOCUS_UP".equals(cap))
                return Direction.UP;
            else if ("FOCUS_DOWN".equals(cap))
                return Direction.DOWN;
            else if ("FOCUS_LEFT".equals(cap))
                return Direction.LEFT;
            else if ("FOCUS_RIGHT".equals(cap))
                return Direction.RIGHT;

            return Direction.NONE;
        }

        @Override
        public int isZoomingTo(String s) {
            String cap = s.toUpperCase();

            if ("ZOOM_UP".equals(cap))
                return Direction.UP;
            else if ("ZOOM_DOWN".equals(cap))
                return Direction.DOWN;
            else if ("ZOOM_LEFT".equals(cap))
                return Direction.LEFT;
            else if ("ZOOM_RIGHT".equals(cap))
                return Direction.RIGHT;

            return Direction.NONE;
        }

        @Override
        public boolean isSelecting(String s) {
            String cap = s.toUpperCase();
            return "SELECT".equals(cap);
        }

        @Override
        public boolean isCanceling(String s) {
            String cap = s.toUpperCase();
            return "CANCEL".equals(cap);
        }

        @Override
        public int isKeyPressed(String s) {
            String cap = s.toUpperCase();

            if ("OK".equals(cap))
                return Operation.KEY_OK;
            else if ("BACK".equals(cap))
                return Operation.KEY_BACK;
            else if ("HOME".equals(cap))
                return Operation.KEY_HOME;
            else if ("VOLUME_DOWN".equals(cap))
                return Operation.KEY_VOLUME_DOWN;
            else if ("VOLUME_UP".equals(cap))
                return Operation.KEY_VOLUME_UP;

            return Operation.NONE;
        }

        @Override
        public int isSpecialOperation(String s) {
            String cap = s.toUpperCase();

            if ("SWITCH".equals(cap))
                return Operation.SWITCH_MODE;
            else if ("SEARCH".equals(cap))
                return Operation.SEARCH;
            else if ("DEEP".equals(cap))
                return Operation.DEEP;
            else if ("EXIT".equals(cap))
                return Operation.TERMINATE;

            return Operation.NONE;
        }
    });

    OperationInputConnector mConnector = new OperationInputConnector(getContext(), IProcessorCommands.TYPE_INPUT_SUPPORT_VOICE) {

        /**
         * 단일 명령 메시지를 전송한다.
         *
         * @param inputRst
         * @return
         */
        @Override
        protected boolean transferDataset(int[] inputRst) {
            Log.d("InputDetectorTest", "Operation: " + inputRst[0] + " / Value1 (Direction) : " + inputRst[1] + " / Value2 (Amount) : " + inputRst[2]);
            return true;
        }

        @Override
        protected boolean transferDataset(int[][] inputRstArray) {
            for(int[] inputRst : inputRstArray)
                Log.d("InputDetectorTest", "Operation: " + inputRst[0] + " / Value1 (Direction) : " + inputRst[1] + " / Value2 (Amount) : " + inputRst[2]);

            return true;
        }
    };
}
