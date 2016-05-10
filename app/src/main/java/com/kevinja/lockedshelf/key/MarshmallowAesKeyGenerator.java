package com.kevinja.lockedshelf.key;

import android.annotation.TargetApi;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 *
 */
public class MarshmallowAesKeyGenerator extends AesKeyGenerator {
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public SecretKey generateSecretKey(String alias) {
        try {
            KeyGenParameterSpec keyGenParameterSpec =
                    new KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_DECRYPT | KeyProperties.PURPOSE_ENCRYPT)
                            .setKeySize(256)
//                            .setRandomizedEncryptionRequired(true)
//                            .setUserAuthenticationRequired(true)
                            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
//                            .setUserAuthenticationValidityDurationSeconds(10 * 60) // Ten minutes.
                            .build();
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES", "AndroidKeyStore");
            keyGenerator.init(keyGenParameterSpec);
            SecretKey secretKey = keyGenerator.generateKey();
            return secretKey;
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
            throw new RuntimeException("Error generating a secret key: " + e.getMessage(), e);
        }
    }
}
