package com.vgd.pokeapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.vgd.pokeapi.errors.ResourceNotFoundError;
import com.vgd.pokeapi.models.Generation;
import com.vgd.pokeapi.models.Pokemon;
import com.vgd.pokeapi.service.PokeService;
import com.vgd.pokeapi.service.PokeServiceFactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IntegrationTest {
    private final PokeService pokeService = PokeServiceFactory.instance().create();

    @ParameterizedTest
    @ValueSource(ints = { 1, 42, 999 })
    public void testGetPokemonById(int id) {
        Pokemon pokemon = pokeService.getPokemon(id);
        assertEquals(id, pokemon.getId());
        log.info("Pokemon with ID {}: {}", id, pokemon.getName());
        log.debug("{}", pokemon);
    }

    @Test
    public void testNonExistentPokemon() {
        assertThrows(ResourceNotFoundError.class, () -> pokeService.getPokemon(9999));
    }

    @ParameterizedTest
    @ValueSource(strings = { "pikachu", "bulbasaur", "charmander", "mewtwo" })
    public void testGetPokemonByName(String name) {
        Pokemon pokemon = pokeService.getPokemon(name);
        assertEquals(name, pokemon.getName());
        log.info("Pokemon with ID {}: {}", pokemon.getId(), name);
        log.debug("{}", pokemon);

    }

    @ParameterizedTest
    @ValueSource(ints = { 1, 4, 9 })
    public void testGetGenerationById(int id) {
        Generation generation = pokeService.getGeneration(id);
        assertEquals(id, generation.getId());
        log.info("Generation with ID {}: {}", id, generation.getName());
        log.debug("{}", generation);

    }

    @ParameterizedTest
    @ValueSource(strings = { "generation-i", "generation-iv", "generation-vii" })
    public void testGetGenerationByName(String name) {
        Generation generation = pokeService.getGeneration(name);
        assertEquals(name, generation.getName());
        log.info("Generation with ID {}: {}", generation.getId(), name);
        log.debug("{}", generation);
    }

    @Test
    public void testPokemonStream() {
        var count = new AtomicLong();
        pokeService.getPokemons()
                .limit(25)
                .forEach(pokemon -> {
                    log.info("Pokemon: {}", pokemon.getName());
                    log.debug("{}", pokemon);
                    count.getAndIncrement();
                });
        log.info("Total pokemon streamed: {}", count.get());
    }

    @Test
    public void getGenerationStream() {
        var count = new AtomicLong();
        pokeService.getGenerations()
                .limit(25)
                .forEach(generation -> {
                    log.info("Generation: {}", generation.getName());
                    log.debug("{}", generation);
                    count.getAndIncrement();
                });
        log.info("Total generations streamed: {}", count.get());
    }

}
