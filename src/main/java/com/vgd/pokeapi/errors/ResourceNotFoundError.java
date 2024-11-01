package com.vgd.pokeapi.errors;

public class ResourceNotFoundError extends PokeApiError {
    public ResourceNotFoundError(String message) {
        super(message);
    }

    public ResourceNotFoundError(String message, Throwable cause) {
        super(message, cause);
    }

}
