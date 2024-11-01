package com.vgd.pokeapi.errors;

public class PokeApiError extends RuntimeException {
    public PokeApiError() {
        super();
    }

    public PokeApiError(int statusCode) {
        super(String.format("API call failed with code %d", statusCode));
    }

    public PokeApiError(String message) {
        super(message);
    }

    public PokeApiError(String message, Throwable cause) {
        super(message, cause);
    }

}
