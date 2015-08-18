package kr.poturns.virtualpalace.communication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by Myungjin Kim on 2015-08-18.
 * IO 작업을 처리하는 메소드들을 모아놓은 유틸 클래스
 */
final class IOUtils {
    /**
     * 주어진 객체를 바이트 배열로 변환한다.
     *
     * @param obj 변환할 객체
     * @return 변환된 바이트 배열
     * @throws IOException 변환할 객체가 {@link Serializable}객체가 아닌 경우, 또는 변환 과정에서 IO Error가 발생한 경우
     */
    public static byte[] toByteArray(Object obj) throws IOException {
        if (!(obj instanceof Serializable))
            throw new IOException("'" + obj.getClass().getSimpleName() + "' instance is not implement Serializable interface!");

        ObjectOutputStream objectOutput = null;
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try {
            objectOutput = new ObjectOutputStream(output);
            objectOutput.writeObject(obj);
            return output.toByteArray();
        } finally {
            closeStream(output);
            closeStream(objectOutput);
        }
    }

    /**
     * 주어진 바이트 배열을 적절한 객체로 변환한다.
     *
     * @param array 변환할 바이트 배열
     * @return 변환된 객체
     * @throws IOException 변환 과정에서 IO Error가 발생한 경우
     *                     ClassNotFoundException  변환될 클래스가 존재하지 않은 경우
     *                     OptionalDataException 변환할 데이터가 객체가 아닌 기본 자료형인 경우
     */
    public static <T extends Serializable> T fromByteArray(byte[] array) throws IOException, ClassNotFoundException {
        ObjectInputStream objectInputStream = null;
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(array);

        try {
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            @SuppressWarnings("unchecked")
            T obj = (T) objectInputStream.readObject();
            return obj;
        } finally {
            closeStream(objectInputStream);
            closeStream(byteArrayInputStream);
        }

    }
    /**
     * 주어진 스트림을 닫는다.
     */
    private static void closeStream(Closeable close) {
        if (close != null) {
            try {
                close.close();
            } catch (IOException ignored) {
                //ignore
            }
        }
    }
}
