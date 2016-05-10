package com.kevinja.lockedshelf.serializer.decrypt;

import android.os.Build;

import javax.crypto.SecretKey;

/**
 *
 */
public abstract class Decryptor {

    protected Decryptor(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    protected SecretKey secretKey;

    public static Decryptor getInstance(SecretKey secretKey, String transformation) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return new MarshmallowDecryptor(secretKey, transformation);
        } else {
            return new LegacyDecryptor(secretKey);
        }
    }

    public abstract byte[] decrypt(final byte[] cipherText);
}
