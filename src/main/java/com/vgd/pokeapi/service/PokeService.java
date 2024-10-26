package com.vgd.pokeapi.service;

import java.util.stream.Stream;

import com.vgd.pokeapi.models.Generation;
import com.vgd.pokeapi.models.Pokemon;

/**
 * Service interface for interacting with the PokeAPI.
 */
public interface PokeService {

    /**
     * Get a generation by its ID.
     * 
     * @param id generation ID.
     * @return the Generation object corresponding to the given ID.
     */
    Generation getGeneration(Integer id);

    /**
     * Get a generation by its name.
     * 
     * @param name generation name.
     * @return the Generation object corresponding to the given name.
     */
    Generation getGeneration(String name);

    /**
     * Get a stream of all generations.
     * 
     * @return a Stream of Generation objects.
     */
    Stream<Generation> getGenerations();

    /**
     * Get a Pokemon by its ID.
     * 
     * @param id Pokemon ID.
     * @return the Pokemon object corresponding to the given ID.
     */
    Pokemon getPokemon(Integer id);

    /**
     * Get a Pokemon by its name.
     * 
     * @param name Pokemon name.
     * @return the Pokemon object corresponding to the given name.
     */
    Pokemon getPokemon(String name);

    /**
     * Get a stream of all Pokemons.
     * 
     * @return a Stream of Pokemon objects.
     */
    Stream<Pokemon> getPokemons();
}
