package kr.poturns.virtualpalace.inputmodule.speech;

import java.util.List;

import kr.poturns.virtualpalace.input.IOperationInputFilter;

/**
 * Created by Myungjin Kim on 2015-07-30.
 * <p>
 * STT 를 통해 얻어진 결과를 적절한 방향 / 명령 으로 해석하는 클래스
 */
public class SpeechInputFilter implements IOperationInputFilter<List<String>> {
    private static final EqualitySpeechFilter EQUALITY_SPEECH_FILTER = new EqualitySpeechFilter();
    //private static final SimilaritySpeechFilter LEVENSHTEIN_SPEECH_FILTER = new SimilaritySpeechFilter();

    @Override
    public int isGoingTo(List<String> strings) {
        return EQUALITY_SPEECH_FILTER.isGoingTo(strings);
        /*
        int op = EQUALITY_SPEECH_FILTER.isGoingTo(strings);

        if (op != Operation.NONE)
            return op;

        return LEVENSHTEIN_SPEECH_FILTER.isGoingTo(strings);
        */
    }

    @Override
    public int isTurningTo(List<String> strings) {
        return EQUALITY_SPEECH_FILTER.isTurningTo(strings);
        /*
        int op = EQUALITY_SPEECH_FILTER.isTurningTo(strings);

        if (op != Operation.NONE)
            return op;

        return LEVENSHTEIN_SPEECH_FILTER.isTurningTo(strings);
        */
    }

    @Override
    public int isFocusingTo(List<String> strings) {
        return EQUALITY_SPEECH_FILTER.isFocusingTo(strings);
        /*
        int op = EQUALITY_SPEECH_FILTER.isFocusingTo(strings);

        if (op != Operation.NONE)
            return op;

        return LEVENSHTEIN_SPEECH_FILTER.isFocusingTo(strings);
        */
    }

    @Override
    public int isZoomingTo(List<String> strings) {
        return EQUALITY_SPEECH_FILTER.isZoomingTo(strings);
        /*
        int op = EQUALITY_SPEECH_FILTER.isZoomingTo(strings);

        if (op != Operation.NONE)
            return op;

        return LEVENSHTEIN_SPEECH_FILTER.isZoomingTo(strings);
        */
    }

    @Override
    public boolean isSelecting(List<String> strings) {
        return EQUALITY_SPEECH_FILTER.isSelecting(strings);//|| LEVENSHTEIN_SPEECH_FILTER.isSelecting(strings);
    }

    @Override
    public boolean isCanceling(List<String> strings) {
        return EQUALITY_SPEECH_FILTER.isCanceling(strings);//|| LEVENSHTEIN_SPEECH_FILTER.isCanceling(strings);
    }

    @Override
    public int isKeyPressed(List<String> strings) {
        return EQUALITY_SPEECH_FILTER.isKeyPressed(strings);
        /*
        int op = EQUALITY_SPEECH_FILTER.isKeyPressed(strings);

        if (op != Operation.NONE)
            return op;

        return LEVENSHTEIN_SPEECH_FILTER.isKeyPressed(strings);
        */
    }

    @Override
    public int isSpecialOperation(List<String> strings) {
        return  EQUALITY_SPEECH_FILTER.isSpecialOperation(strings);
        /*
        int op = EQUALITY_SPEECH_FILTER.isSpecialOperation(strings);

        if (op != Operation.NONE)
            return op;

        return LEVENSHTEIN_SPEECH_FILTER.isSpecialOperation(strings);
        */
    }
}
