package com.kevinja.lockedshelf.serializer;

/**
 *
 */
public abstract class AbstractLockedSerializer implements LockedSerializer {
    protected String alias;

    public AbstractLockedSerializer(String alias) {
        this.alias = alias;
    }

    public abstract byte[] decrypt(byte[] cipherText);
    public abstract byte[] encrypt(byte[] clearText);
}
