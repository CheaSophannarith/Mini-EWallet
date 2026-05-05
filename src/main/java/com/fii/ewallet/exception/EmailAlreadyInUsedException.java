package com.fii.ewallet.exception;

public class EmailAlreadyInUsedException extends RuntimeException {

    public EmailAlreadyInUsedException(String message) {
        super(message);
    }

}
