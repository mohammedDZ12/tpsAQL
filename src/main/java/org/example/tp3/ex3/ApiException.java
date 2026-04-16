package org.example.tp3.ex3;
public class ApiException extends RuntimeException {
    public ApiException(String message) {
        super(message);
    }
    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
