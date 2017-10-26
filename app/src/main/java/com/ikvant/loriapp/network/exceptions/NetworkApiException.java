package com.ikvant.loriapp.network.exceptions;

/**
 * Created by ikvant.
 */
public class NetworkApiException extends Exception {
    public NetworkApiException() {
        super();
    }

    public NetworkApiException(String message) {
        super(message);
    }

    public NetworkApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public NetworkApiException(Throwable cause) {
        super(cause);
    }
}
