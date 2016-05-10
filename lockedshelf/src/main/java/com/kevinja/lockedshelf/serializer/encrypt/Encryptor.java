package com.kevinja.lockedshelf.serializer.encrypt;

import android.os.Build;

import javax.crypto.SecretKey;

/**
 * Created by kevinwallace on 2016-05-03.
 */
public abstract class Encryptor {

    protected SecretKey secretKey;

    protected Encryptor(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    public static Encryptor getInstance(SecretKey secretKey, String transformation) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return new MarshmallowEncryptor(secretKey, transformation);
        } else {
            return new LegacyEncryptor(secretKey);
        }
    }

    public abstract byte[] encrypt(byte[] clearText);
}
