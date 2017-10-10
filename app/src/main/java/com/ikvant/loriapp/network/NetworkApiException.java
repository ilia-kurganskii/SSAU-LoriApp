package com.ikvant.loriapp.network;

/**
 * Created by ikvant.
 */
public class NetworkApiException extends Exception {
    public NetworkApiException() {
        super();
    }

    public NetworkApiException(String message, Throwable cause) {
        super(message, cause);
    }

    NetworkApiException(Throwable cause) {
        super(cause);
    }
}
