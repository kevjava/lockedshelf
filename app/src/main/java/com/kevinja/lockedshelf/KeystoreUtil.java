package com.kevinja.lockedshelf;

import android.app.Activity;
import android.os.Build;
import android.util.Log;

import com.kevinja.lockedshelf.device.DeviceLockManager;
import com.kevinja.lockedshelf.key.AesKeyGenerator;
import com.kevinja.lockedshelf.key.KeyRetriever;

import org.nick.androidkeystore.PRNGFixes;
import org.nick.androidkeystore.android.security.KeyStoreJb;
import org.nick.androidkeystore.android.security.KeyStoreJb43;
import org.nick.androidkeystore.android.security.KeyStoreKk;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.crypto.SecretKey;

/**
 *
 */
public class KeystoreUtil {
    private static final int REQUEST_CODE_UNLOCK = 12;

    public static SecretKey getSecretKey(String alias) {
        KeyRetriever keyRetriever = KeyRetriever.getInstance();
        return keyRetriever.getSecretKey(alias);
    }

    public static KeyStore getAndroidKeyStore() {
        try {
            KeyStore keystore = KeyStore.getInstance("AndroidKeyStore");
            keystore.load(null);
            return keystore;
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            throw new RuntimeException("Error getting keystore: " + e.getMessage(), e);
        }
    }

    /**
     * Can be called under any platform to generate an appropriate AES key.
     *
     * @param alias
     * @return
     */
    public static SecretKey generateSecretKey(String alias) {
        AesKeyGenerator keyGenerator = AesKeyGenerator.getInstance();
        return keyGenerator.generateSecretKey(alias);
    }

    // TODO: Move inside KeyStoreJb.
    public static void checkRc(KeyStoreJb keystore, boolean success) {
        if (!success) {
            String errorStr = rcToStr(keystore.getLastError());

            throw new RuntimeException("Keystore error: " + errorStr);
        }
    }

    private static String rcToStr(int rc) {
        switch (rc) {
            case KeyStoreJb.NO_ERROR:
                return "NO_ERROR";
            case KeyStoreJb.LOCKED:
                return "LOCKED";
            case KeyStoreJb.UNINITIALIZED:
                return "UNINITIALIZED";
            case KeyStoreJb.SYSTEM_ERROR:
                return "SYSTEM_ERROR";
            case KeyStoreJb.PROTOCOL_ERROR:
                return "PROTOCOL_ERROR";
            case KeyStoreJb.PERMISSION_DENIED:
                return "PERMISSION_DENIED";
            case KeyStoreJb.KEY_NOT_FOUND:
                return "KEY_NOT_FOUND";
            case KeyStoreJb.VALUE_CORRUPTED:
                return "VALUE_CORRUPTED";
            case KeyStoreJb.UNDEFINED_ACTION:
                return "UNDEFINED_ACTION";
            case KeyStoreJb.WRONG_PASSWORD:
                return "WRONG_PASSWORD";
            default:
                return "Unknown RC";
        }
    }

    public static boolean isKeyStoreReady() {
        boolean ready = false;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                KeyStore keystore = getAndroidKeyStore();
                keystore.aliases(); // TODO: Is this necessary?
                ready = true;
            } else {
                KeyStoreJb keystore = getLegacyKeyStore();
                KeyStoreJb.State state = keystore.state();
                ready = (state == KeyStoreJb.State.UNLOCKED);
            }
        } catch (Throwable t) {
            Log.v("KeyStoreUtil", "Error returned by keystore: " + t.getMessage());
        }
        return ready;
    }

    public static void ensureKeyguardIsUnlocked(Activity activity) {
        DeviceLockManager deviceLockManager = DeviceLockManager.getInstance(activity);
        if (deviceLockManager.isDeviceLocked()) {
            deviceLockManager.startUnlockActivity();
        }
    }

    public static boolean isDeviceSecure(Activity activity) {
        return DeviceLockManager.getInstance(activity).isDeviceSecured();
    }

    public static KeyStoreJb getLegacyKeyStore() {
        PRNGFixes.apply();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return KeyStoreKk.getInstance();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return KeyStoreJb43.getInstance();
        } else {
            return KeyStoreJb.getInstance();
        }
    }
}
