package com.kevinja.lockedshelf.serializer;

import android.util.Log;

import com.google.gson.Gson;
import com.kevinja.lockedshelf.serializer.decrypt.Decryptor;
import com.kevinja.lockedshelf.serializer.encrypt.Encryptor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.crypto.SecretKey;

/**
 * TODO: Send checked exception on cache read/write errors.
 */
public class LockedGsonSerializer extends AbstractLockedSerializer {

    private final Gson gson;

    static public final String AES_CBC_NOPADDING = "AES/CBC/PKCS7Padding";
    private String transformation = AES_CBC_NOPADDING;

    private final SecretKey secretKey;

    private final File dir;

    private final String IV_EXT = ".iv";

    public LockedGsonSerializer(final File dir, final String id, final Gson gson, final SecretKey secretKey) {
        super(id);
        this.dir = dir;
        this.secretKey = secretKey;
        this.gson = gson;
    }

    @Override
    public <T> T deserialize(final InputStream inputStream, final Class<T> type) {
        byte[] cipherText = readFully(inputStream);
        if (cipherText == null || cipherText.length == 0) {
            return null;
        }
        String clearText = new String(decrypt(cipherText));
        return gson.fromJson(clearText, type);
    }

    /**
     * Reads the input stream into a byte array, using a buffer size of 1K.
     * @param inputStream Stream to be read in.
     * @return the raw bytes contained within that stream.
     */
    private byte[] readFully(final InputStream inputStream) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        int bytesRead;
        byte[] buffer = new byte[1024];

        try {
            while ((bytesRead = inputStream.read(buffer, 0, buffer.length)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading shelf item: " + e.getMessage(), e);
        }

        return output.toByteArray();
    }

    @Override
    public <T> void serialize(final OutputStream outputStream, final T object) {
        String clearText = gson.toJson(object);
        byte[] cipherText = encrypt(clearText.getBytes());
        try {
            outputStream.write(cipherText);
        } catch (IOException e) {
            throw new RuntimeException("Error writing output shelf item: " + e.getMessage(), e);
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                // Ignore.
            }
        }
    }

    @Override
    public byte[] decrypt(final byte[] cipherText) {
        return Decryptor.getInstance(secretKey, transformation).decrypt(cipherText);
    }

    @Override
    public byte[] encrypt(final byte[] clearText) {
        return Encryptor.getInstance(secretKey, transformation).encrypt(clearText);
    }

    protected File getFile(final String key) {
        return new File(dir, key + IV_EXT); //Getting a file within the dir
    }

    protected FileOutputStream getIVFileOutputStream() {
        try {
            return new FileOutputStream(getFile(alias));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Error writing IV to output file: " + e.getMessage(), e);
        }
    }

    protected FileInputStream getIVFileInputStream() throws FileNotFoundException {
        return new FileInputStream(getFile(alias));
    }

    protected byte[] readIV() {
        try {
            return readFully(getIVFileInputStream());
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    protected void writeIV(final byte[] iv) {
        OutputStream outputStream = getIVFileOutputStream();
        try {
            outputStream.write(iv);
        } catch (IOException e) {
            throw new RuntimeException("Error writing output shelf item: " + e.getMessage(), e);
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                // Ignore.
            }
        }

    }
}
