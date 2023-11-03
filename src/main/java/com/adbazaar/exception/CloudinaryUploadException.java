package com.adbazaar.exception;

import java.io.IOException;

public class CloudinaryUploadException extends RuntimeException {

    public CloudinaryUploadException(String message) {
        super(message);
    }

    public CloudinaryUploadException(String message, IOException e) {
        super(message, e);
    }
}
