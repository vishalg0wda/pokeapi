package com.vgd.pokeapi.retrofit;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.vgd.pokeapi.models.Generation;
import com.vgd.pokeapi.models.NamedApiResource;
import com.vgd.pokeapi.models.Pokemon;
import com.vgd.pokeapi.service.PokeService;

import io.github.resilience4j.retry.Retry;
import lombok.SneakyThrows;
import retrofit2.Retrofit;

/**
 * RetrofitPokeService is an implementation of the PokeService interface that
 * uses Retrofit
 * to make HTTP requests to the PokeAPI. It also uses Resilience4j for retrying
 * failed requests.
 */
public class RetrofitPokeService implements PokeService {
    private final PokeApi pokeApi;
    private final Retry retrier;
    private final Integer pageSize;

    public RetrofitPokeService(Retrofit retrofit, Retry retrier, Integer pageSize) {
        this.pokeApi = retrofit.create(PokeApi.class);
        this.retrier = retrier;
        this.pageSize = pageSize;
    }

    @Override
    @SneakyThrows
    public Generation getGeneration(Integer id) {
        return Retry.decorateCheckedSupplier(retrier,
                () -> pokeApi.getGeneration(id).execute().body()).get();
    }

    @Override
    @SneakyThrows
    public Generation getGeneration(String name) {
        return Retry.decorateCheckedSupplier(retrier,
                () -> pokeApi.getGeneration(name).execute().body()).get();
    }

    @Override
    @SneakyThrows
    public Stream<Generation> getGenerations() {
        return getNamedApiResources("generation")
                .map(resource -> getGeneration(resource.getName()));
    }

    @Override
    @SneakyThrows
    public Pokemon getPokemon(Integer id) {
        return Retry.decorateCheckedSupplier(retrier,
                () -> pokeApi.getPokemon(id).execute().body()).get();
    }

    @Override
    @SneakyThrows
    public Pokemon getPokemon(String name) {
        return Retry.decorateCheckedSupplier(retrier,
                () -> pokeApi.getPokemon(name).execute().body()).get();
    }

    @Override
    @SneakyThrows
    public Stream<Pokemon> getPokemons() {
        return getNamedApiResources("pokemon")
                .map(resource -> getPokemon(resource.getName()));
    }

    private Stream<NamedApiResource> getNamedApiResources(String namedResource) {
        Iterable<NamedApiResource> iterable = () -> new NamedApiResourceIterator(
                pokeApi, namedResource, pageSize, retrier);
        return StreamSupport.stream(iterable.spliterator(), false);
    }

}