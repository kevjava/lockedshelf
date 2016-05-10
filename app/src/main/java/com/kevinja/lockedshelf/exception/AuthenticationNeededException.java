package com.kevinja.lockedshelf.exception;

/**
 * Created by kevinwallace on 2016-05-02.
 */
public class AuthenticationNeededException extends RuntimeException {
    public AuthenticationNeededException(Throwable t) {
        super(t);
    }
}
