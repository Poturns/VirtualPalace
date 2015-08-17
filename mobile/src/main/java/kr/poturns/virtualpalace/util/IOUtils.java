package kr.poturns.virtualpalace.util;


import android.content.Context;
import android.graphics.Bitmap;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;

/**
 * Created by Myungjin Kim on 2015-08-16
 * IO 작업을 처리하는 메소드들을 모아놓은 유틸 클래스
 */
public final class IOUtils {

    private IOUtils() {
    }

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
     * 비트맵을 파일에 저장한다.
     *
     * @param context  임시 디렉토리를 불러올 Context
     * @param fileName 저장될 파일의 이름
     * @param bitmap   저장될 비트맵
     * @return 저장된 파일의 경로
     */
    public static String bitmapToFile(Context context, String fileName, Bitmap bitmap) throws IOException {
        File f = File.createTempFile(fileName, null, context.getCacheDir());
        FileOutputStream fileOutputStream = new FileOutputStream(f);
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            return f.getAbsolutePath();
        } finally {
            closeStream(fileOutputStream);
        }
    }

    /**
     * 스트림에 주어진 내용을 기록한다.
     *
     * @param out 내용이 기록될 {@link OutputStream}
     * @param str 스트림에 기록할 내용
     * @throws IOException 스트림에 내용을 기록하는 도중에 IO 에러가 발생한 경우
     */
    public static void writeContentToStream(OutputStream out, String str) throws IOException {
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(out);
        BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
        try {
            bufferedWriter.write(str);
        } finally {
            closeStream(bufferedWriter);
            closeStream(outputStreamWriter);
            closeStream(out);
        }
    }

    /**
     * 스트림에 주어진 내용을 기록한다.
     *
     * @param out  내용이 기록될 {@link OutputStream}
     * @param file 스트림에 기록할 내용이 존재하는 파일
     * @throws IOException 스트림에 내용을 기록하는 도중에 IO 에러가 발생한 경우
     */
    public static void writeContentToStream(OutputStream out, File file) throws IOException {
        writeContentToStream(out, readContent(new FileInputStream(file)));
    }

    /**
     * 스트림에 주어진 내용을 기록한다.
     *
     * @param out    내용이 기록될 {@link OutputStream}
     * @param buffer 스트림에 기록할 내용
     * @throws IOException 스트림에 내용을 기록하는 도중에 IO 에러가 발생한 경우
     */
    public static void writeContentToStream(OutputStream out, byte[] buffer) throws IOException {
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(out);

        try {
            bufferedOutputStream.write(buffer);
        } finally {
            closeStream(bufferedOutputStream);
            closeStream(out);
        }
    }

    /**
     * 주어진 스트림으로 부터 내용을 읽어 바이트 배열로 반환한다.
     *
     * @param in 내용을 읽을 {@link InputStream}
     * @return 스트림으로 부터 읽은 내용이 기록된 바이트 배열
     * @throws IOException 스트림에서 내용을 읽는 도중에 IO 에러가 발생한 경우
     */
    public static byte[] readContent(InputStream in) throws IOException {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            byte[] readByte = new byte[1024];
            int readLen;
            while ((readLen = in.read(readByte)) != -1) {
                outputStream.write(readByte, 0, readLen);
            }
            return outputStream.toByteArray();
        } finally {
            closeStream(outputStream);
            closeStream(bufferedInputStream);
            closeStream(in);
        }
    }

    /**
     * 주어진 스트림으로 부터 내용을 읽어 문자열로 반환한다.
     *
     * @param in       내용을 읽을 {@link InputStream}
     * @param encoding 반환될 문자열의 인코딩
     * @return 스트림으로 부터 읽은 문자열
     * @throws IOException 스트림에서 내용을 읽는 도중에 IO 에러가 발생한 경우
     */
    public static String readStringContent(InputStream in, String encoding) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, encoding));
        try {
            StringBuilder builder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                builder.append(line).append('\n');
            }
            return builder.toString();
        } finally {
            closeStream(reader);
            closeStream(in);
        }
    }

    /**
     * 주어진 스트림으로 부터 내용을 읽어 파일에 기록한다.
     *
     * @param in      내용을 읽을 {@link InputStream}
     * @param outFile 내용이 기록될 파일
     * @throws IOException 스트림 IO 작업 도중에 IO 에러가 발생한 경우
     */
    public static void readContentToFile(InputStream in, File outFile) throws IOException {
        writeContentToStream(new FileOutputStream(outFile), readContent(in));
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
