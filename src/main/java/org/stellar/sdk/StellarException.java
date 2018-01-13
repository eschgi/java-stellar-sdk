package org.stellar.sdk;

public class StellarException extends RuntimeException {
    public StellarException() {
        super();
    }

    public StellarException(String message) {
        super(message);
    }
}
