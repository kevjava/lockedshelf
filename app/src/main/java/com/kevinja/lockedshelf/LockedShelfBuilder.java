package com.kevinja.lockedshelf;

import android.content.Context;

import com.google.gson.Gson;
import com.toddway.shelf.Shelf;
import com.toddway.shelf.serializer.Serializer;
import com.toddway.shelf.storage.FileStorage;
import com.kevinja.lockedshelf.serializer.LockedGsonSerializer;

import java.io.File;
import java.security.KeyStore;
import java.util.concurrent.TimeUnit;

import javax.crypto.SecretKey;

/**
 * Created by kevinwallace on 2016-04-27.
 */
public class LockedShelfBuilder {
    /**
     * Context to which this {@link Shelf}'s keystore is locked.
     */
    Context context;

    /**
     * The base directory used for this Shelf.  Will append a "/[id]" if the ID is set.
     */
    File dir;

    /**
     * Id to be used as the last directory element for this Shelf.  Note that each id gets its
     * own encryption key.
     */
    String id;

    /**
     * Alternate keystore to be used.  Defaults to the standard Android keystore.
     */
    KeyStore keystore;

    /**
     * Encryption key to be used for encryption and decryption for this shelf.  Defaults to the
     * one stored in the keystore.
     */
    SecretKey secretKey;

    /**
     * Serializer to be used for this shelf.  Defaults to a {@link LockedGsonSerializer}.
     */
    Serializer serializer;

    /**
     * Default lifetime for items in this shelf.
     */
    Long defaultLifetime;

    /**
     * Time unit to be used for the default lifetime.
     */
    TimeUnit defaultLifetimeUnit;

    /**
     * Well cause a SecretKey to be generated for this id, if it is not found in the keystore.
     *
     * By default, we want the caller to be aware if we are generating a secret key on their behalf.
     */
    boolean generateKeyIfNotFound = false;

    /**
     * Default constructor.  The <code>dir</code> is the only field that can't be defaulted.
     *
     * @param context Context used for storage of the keys in the store.
     */
    public LockedShelfBuilder(Context context) { // TODO: Remove context.  Switch to File.
        this.context = context;
        this.dir = context.getCacheDir();
    }

    public LockedShelfBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public LockedShelfBuilder setKeystore(KeyStore keystore) {
        this.keystore = keystore;
        return this;
    }

    public LockedShelfBuilder setSecretKey(SecretKey secretKey) {
        this.secretKey = secretKey;
        return this;
    }

    public LockedShelfBuilder setSerializer(Serializer serializer) {
        this.serializer = serializer;
        return this;
    }

    public LockedShelfBuilder setDefaultLifetime(long defaultLifetime, TimeUnit defaultLifetimeUnit) {
        this.defaultLifetime = defaultLifetime;
        this.defaultLifetimeUnit = defaultLifetimeUnit;
        return this;
    }

    public LockedShelfBuilder setGenerateKeyIfNotFound(boolean generateKeyIfNotFound) {
        this.generateKeyIfNotFound = generateKeyIfNotFound;
        return this;
    }

    public Shelf build() {
        Shelf lockedShelf = new Shelf(
                new FileStorage(getShelfDir(),
                        getSerializer(getSecretKey()),
                        getDefaultLifetimeInMillis()
                )
        );
        return lockedShelf;
    }

    private File getShelfDir() {
        if (id != null && !id.isEmpty()) {
            return new File(dir, id);
        } else {
            return dir;
        }
    }

    private SecretKey getSecretKey() {
        if (secretKey != null) {
            return secretKey;
        }

        SecretKey secretKey;
        if (keystore == null) {
            keystore = KeystoreUtil.getAndroidKeyStore();
        }
        secretKey = KeystoreUtil.getSecretKey(id);
        if (secretKey == null && generateKeyIfNotFound) {
            secretKey = KeystoreUtil.generateSecretKey(id);
        } else if (secretKey == null) {
            throw new RuntimeException("No key for id " + id +
                    " found in the keystore.  Call .setGenerateKeyIfNotFound() if you wish to " +
                    "generate one in this case.");
        }
        return secretKey;
    }

    private Serializer getSerializer(SecretKey secretKey) {
        if (serializer == null) {
            serializer = getDefaultSerializer(secretKey);
        }
        return serializer;
    }

    private Serializer getDefaultSerializer(SecretKey secretKey) {
        return new LockedGsonSerializer(getShelfDir(), id, new Gson(), secretKey);
    }

    public Long getDefaultLifetimeInMillis() {
        if (defaultLifetimeUnit == null || defaultLifetime == null) {
            defaultLifetime = 1L;
            defaultLifetimeUnit = TimeUnit.MILLISECONDS;
        }
        return defaultLifetimeUnit.toMillis(defaultLifetime);
    }
}
