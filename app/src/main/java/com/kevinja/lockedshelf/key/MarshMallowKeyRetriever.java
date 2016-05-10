package com.kevinja.lockedshelf.key;

import com.kevinja.lockedshelf.KeystoreUtil;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;

import javax.crypto.SecretKey;

/**
 * Created by kevinwallace on 2016-05-04.
 */
public class MarshMallowKeyRetriever extends KeyRetriever {
    public MarshMallowKeyRetriever() {
        super();
    }

    public SecretKey getSecretKey(String alias)
    {
        try {
            KeyStore keyStore = KeystoreUtil.getAndroidKeyStore();
            KeyStore.Entry entry = keyStore.getEntry(alias, null);
            if (entry == null) {
                return null;
            }
            return ((KeyStore.SecretKeyEntry) entry).getSecretKey(); // Cast failure is non-recoverable.
        } catch (NoSuchAlgorithmException | UnrecoverableEntryException | KeyStoreException e) {
            throw new RuntimeException("Error retrieving key from keystore: " + e.getMessage(), e);
        }
    }
}
