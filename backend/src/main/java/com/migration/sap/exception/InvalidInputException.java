package com.migration.sap.exception;

/**
 * Replaces ABAP RAISE invalid_input from ZFM_GET_MAT_SO_DETAILS..FUGR.txt line 32.
 */
public class InvalidInputException extends RuntimeException {

    public InvalidInputException(String message) {
        super(message);
    }
}
