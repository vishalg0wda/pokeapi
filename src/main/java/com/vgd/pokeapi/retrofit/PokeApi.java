package com.vgd.pokeapi.retrofit;

import java.util.concurrent.CompletableFuture;

import com.vgd.pokeapi.models.Generation;
import com.vgd.pokeapi.models.NamedApiResourceList;
import com.vgd.pokeapi.models.Pokemon;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

interface PokeApi {
    @GET("generation/{id}")
    public Call<Generation> getGeneration(@Path("id") Integer id);

    @GET("generation/{id}")
    public CompletableFuture<Generation> getGenerationAsync(@Path("id") Integer id);

    @GET("generation/{name}")
    public Call<Generation> getGeneration(@Path("name") String name);

    @GET("pokemon/{id}")
    public Call<Pokemon> getPokemon(@Path("id") Integer id);

    @GET("pokemon/{name}")
    public Call<Pokemon> getPokemon(@Path("name") String name);

    @GET("{named_resource}")
    public Call<NamedApiResourceList> getResourceList(
            @Path("named_resource") String namedResource,
            @Query("limit") Integer limit,
            @Query("offset") Integer offset);
}