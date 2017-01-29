package org.ebay.datameta.dom;

/**
 * @author Michael Bergens
 */
public class VerificationException extends RuntimeException {
    public VerificationException(String message) {
        super(message);
    }

    public VerificationException(String message, Throwable cause) {
        super(message, cause);
    }
}
