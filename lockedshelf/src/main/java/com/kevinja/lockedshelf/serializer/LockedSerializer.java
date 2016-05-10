package com.kevinja.lockedshelf.serializer;

import com.toddway.shelf.serializer.Serializer;

/**
 *
 */
public interface LockedSerializer extends Serializer {
    byte[] decrypt(byte[] cipherText);
    byte[] encrypt(byte[] clearText);
}
