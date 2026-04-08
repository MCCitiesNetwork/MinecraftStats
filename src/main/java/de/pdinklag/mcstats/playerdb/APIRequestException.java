package de.pdinklag.mcstats.playerdb;

/**
 * An exception raised while processing a PlayerDB API request.
 */
public class APIRequestException extends RuntimeException {
    APIRequestException() {
    }

    APIRequestException(String message) {
        super(message);
    }

    APIRequestException(Throwable cause) {
        super(cause);
    }

    APIRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
