package de.pdinklag.mcstats.minetools;

/**
 * An exception raised while processing a Minetools.eu API request.
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
