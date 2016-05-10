package com.kevinja.lockedshelf.serializer.encrypt;

import android.annotation.TargetApi;
import android.os.Build;
import android.security.keystore.UserNotAuthenticatedException;

import com.kevinja.lockedshelf.exception.AuthenticationNeededException;

import org.nick.androidkeystore.Crypto;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/**
 * Created by kevinwallace on 2016-05-03.
 */
public class MarshmallowEncryptor extends Encryptor {
    private static final Object DELIMITER = "]";
    private final String transformation;

    private byte[] iv;

    public MarshmallowEncryptor(SecretKey secretKey, String transformation) {
        super(secretKey);
        this.transformation = transformation;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public byte[] encrypt(byte[] clearText) {
        try {
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] cipherText = cipher.doFinal(Crypto.toBase64(clearText).getBytes());
            this.iv = cipher.getIV();

            String ret = String.format("%s%s%s", Crypto.toBase64(iv), DELIMITER, Crypto.toBase64(cipherText));
            return ret.getBytes();
        } catch (UserNotAuthenticatedException e) {
            throw new AuthenticationNeededException(e);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            throw new RuntimeException("There was an error encrypting Shelf storage: " + e.getMessage(), e);
        }
    }
}
