package com.kevinja.lockedshelf.key;

import android.os.Build;

import javax.crypto.SecretKey;

/**
 * Created by kevinwallace on 2016-05-04.
 */
public abstract class AesKeyGenerator {

    protected AesKeyGenerator() {
    }

    public static AesKeyGenerator getInstance() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return new MarshmallowAesKeyGenerator();
        } else {
            return new LegacyAesKeyGenerator();
        }
    }

    public abstract SecretKey generateSecretKey(String alias);
}
