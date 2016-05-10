package com.kevinja.lockedshelf.key;

import com.kevinja.lockedshelf.KeystoreUtil;

import org.nick.androidkeystore.Crypto;
import org.nick.androidkeystore.android.security.KeyStoreJb;

import javax.crypto.SecretKey;

/**
 * Created by kevinwallace on 2016-05-04.
 */
public class LegacyAesKeyGenerator extends AesKeyGenerator {
    @Override
    public SecretKey generateSecretKey(String alias) {
        KeyStoreJb keystore = KeystoreUtil.getLegacyKeyStore();
        KeyStoreJb.State state = keystore.state();
//            if (KeyStoreJb.State.UNLOCKED != state) {
//                throw new RuntimeException("Keystore isn't unlocked.  State: " + state);
//            }
        SecretKey secretKey = Crypto.generateAesKey();

        boolean success = keystore.put(alias, secretKey.getEncoded());
        KeystoreUtil.checkRc(keystore, success);

        return secretKey;
    }
}
