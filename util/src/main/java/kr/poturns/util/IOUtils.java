package kr.poturns.util;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;

public class IOUtils {

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

    private static void closeStream(Closeable close) {
        if (close != null) {
            try {
                close.close();
            } catch (IOException ignored) {
                //ignore
            }
        }
    }

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

    public static void writeContentToStream(OutputStream out, byte[] buffer) throws IOException {
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(out);

        try {
            bufferedOutputStream.write(buffer);
        } finally {
            closeStream(bufferedOutputStream);
            closeStream(out);
        }
    }

    public static byte[] readContentFromStream(InputStream in) throws IOException {
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

    public static String readContentFromStream(InputStream in, String encoding) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, encoding));
        try {
            StringBuilder builder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                builder.append(line).append('\n');
            }
            return builder.toString();
        } finally {
            reader.close();
            closeStream(in);
        }
    }

}
