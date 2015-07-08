package kr.poturns.util;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class DataUtil {

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
