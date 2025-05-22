package com.example.pixel.exception;

public class BatchProcessingInterruptedException extends RuntimeException {

    public BatchProcessingInterruptedException(String message) {
        super(message);
    }

    public BatchProcessingInterruptedException(String message, Throwable cause) {
        super(message, cause);
    }
}
