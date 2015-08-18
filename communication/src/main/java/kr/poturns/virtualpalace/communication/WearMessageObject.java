package kr.poturns.virtualpalace.communication;

import java.io.IOException;
import java.io.Serializable;

/**
 * Created by Myungjin Kim on 2015-08-18.
 * <p/>
 * wear - mobile 간 주고 받을 메시지
 */
public class WearMessageObject implements Serializable {
    private static final long serialVersionUID = -8072297076856926834L;
    public int[][] dataSet = null;

    /**
     * 바이트 배열로부터 메시지 객체를 생성한다.
     *
     * @param bytes 메시지 정보가 담긴 바이트 배열
     * @return 바이트 배열로부터 변환된 메시지 객체
     */
    public static WearMessageObject fromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
        return IOUtils.fromByteArray(bytes);
    }

    /**
     * 전송을 위해 바이트 배열로 변환한다.
     *
     * @return 변환된 바이트 배열
     */
    public byte[] toBytes() throws IOException {
        return IOUtils.toByteArray(this);
    }


}
