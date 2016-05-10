package com.kevinja.lockedshelf.key;

import com.kevinja.lockedshelf.KeystoreUtil;

import org.nick.androidkeystore.android.security.KeyStoreJb;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by kevinwallace on 2016-05-04.
 */
public class LegacyKeyRetriever extends KeyRetriever {
    protected LegacyKeyRetriever() {
        super();
    }

    @Override
    public SecretKey getSecretKey(String alias) {
        KeyStoreJb keyStore = KeystoreUtil.getLegacyKeyStore();
        byte[] keyBytes = keyStore.get(alias);
        if (keyBytes == null) {
            return null;
        }

        SecretKey key = new SecretKeySpec(keyBytes, "AES");
        return key;
    }
}
