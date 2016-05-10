package com.kevinja.lockedshelf.serializer.decrypt;

import org.nick.androidkeystore.Crypto;

import javax.crypto.SecretKey;

/**
 * Created by kevinwallace on 2016-05-03.
 */
public class LegacyDecryptor extends Decryptor {
    public LegacyDecryptor(SecretKey secretKey) {
        super(secretKey);
    }

    @Override
    public byte[] decrypt(byte[] cipherText) {
        if (cipherText == null) {
            return null;
        }

        String clearText = Crypto.decryptAesCbc(new String(cipherText), secretKey);
        if (clearText == null) {
            return null;
        }

        return clearText.getBytes();
    }

}
