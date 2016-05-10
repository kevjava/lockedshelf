package com.kevinja.lockedshelf.key;

import android.os.Build;

import javax.crypto.SecretKey;

/**
 * Created by kevinwallace on 2016-05-04.
 */
public abstract class KeyRetriever {

    protected KeyRetriever() {
    }

    public static KeyRetriever getInstance() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return new MarshMallowKeyRetriever();
        } else {
            return new LegacyKeyRetriever();
        }
    }

    public abstract SecretKey getSecretKey(String alias);


}
