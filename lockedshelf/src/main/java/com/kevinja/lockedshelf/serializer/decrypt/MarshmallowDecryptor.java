package com.kevinja.lockedshelf.serializer.decrypt;

import android.annotation.TargetApi;
import android.os.Build;
import android.security.keystore.UserNotAuthenticatedException;

import com.kevinja.lockedshelf.exception.AuthenticationNeededException;

import org.nick.androidkeystore.Crypto;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

/**
 * Created by kevinwallace on 2016-05-03.
 */
public class MarshmallowDecryptor extends Decryptor {
    private final String transformation;

    private final String DELIMITER = "]";

    public MarshmallowDecryptor(SecretKey secretKey, String transformation) {
        super(secretKey);
        this.transformation = transformation; // FIXME: Hard-code this.
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public byte[] decrypt(byte[] cipherText) {
        if (cipherText == null) {
            return null;
        }

        try {
            String cipherString = new String(cipherText);
            String[] fields = cipherString.split(DELIMITER);

            byte[] iv = Crypto.fromBase64(fields[0]);
            byte[] cipherBytes = Crypto.fromBase64(fields[1]);

            Cipher cipher = Cipher.getInstance(transformation);
            IvParameterSpec ivParams = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParams);

            byte[] clearText = cipher.doFinal(cipherBytes);

            return Crypto.fromBase64(new String(clearText));
        } catch (UserNotAuthenticatedException e) {
            throw new AuthenticationNeededException(e);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException e) {
            throw new RuntimeException("There was an error decrypting Shelf storage: " + e.getMessage(), e);
        }
    }
}
