package com.vgd.pokeapi.errors;

public class TooManyRequestsError extends PokeApiError {
    public TooManyRequestsError(String message) {
        super(message);
    }

    public TooManyRequestsError(String message, Throwable cause) {
        super(message, cause);
    }

}
