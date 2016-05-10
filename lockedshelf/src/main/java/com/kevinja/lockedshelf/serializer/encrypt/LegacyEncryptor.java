package com.kevinja.lockedshelf.serializer.encrypt;

import org.nick.androidkeystore.Crypto;

import javax.crypto.SecretKey;

/**
 * Created by kevinwallace on 2016-05-03.
 */
public class LegacyEncryptor extends Encryptor {
    public LegacyEncryptor(SecretKey secretKey) {
        super(secretKey);
    }

    @Override
    public byte[] encrypt(byte[] clearText) {
        if (clearText == null) {
            return null;
        }

        String cipherText = Crypto.encryptAesCbc(new String(clearText), secretKey);
        if (cipherText == null) {
            return null;
        }

        return cipherText.getBytes();
    }
}
