package com.vgd.pokeapi.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.vgd.pokeapi.errors.PokeApiError;
import com.vgd.pokeapi.errors.ResourceNotFoundError;
import com.vgd.pokeapi.errors.TooManyRequestsError;
import com.vgd.pokeapi.retrofit.RetrofitPokeService;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import lombok.NonNull;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Singleton factory for creating {@link PokeService} instances.
 */
public class PokeServiceFactory {
    private static PokeServiceFactory instance;

    public static PokeServiceFactory instance() {
        if (instance == null) {
            instance = new PokeServiceFactory();
        }

        return instance;
    }

    private Config overrides;

    /**
     * Set configuration overrides.
     * 
     * @param overrides sparse configuration to override defaults.
     * @return factory instance.
     */
    public PokeServiceFactory withOverrides(@NonNull Config overrides) {
        this.overrides = overrides;
        return this;
    }

    private Config reconcileConfig() {
        Config config = ConfigFactory.load("poke-sdk.conf");
        if (overrides != null) {
            return overrides.withFallback(config);
        }

        return config;
    }

    private Retry retrier(Config config) {
        return Retry.of("retrier", RetryConfig.custom()
                .maxAttempts(config.getInt("poke-api.retry.max-attempts"))
                .waitDuration(config.getDuration("poke-api.retry.wait-duration"))
                .retryOnException(e -> {
                    // No point retrying on 404 errors
                    if (e instanceof ResourceNotFoundError) {
                    return false;
                    }
                    // Any other error is worth retrying.
                    // Can probably be more granular here.
                    return true;
                })
                .build());
    }

    /**
     * Create a new {@link OkHttpClient} instance using the factory configuration.
     * We configure OkHttpClient to explicitly throw exceptions for non-200
     * responses.
     * 
     * @param config
     * @return
     */
    private OkHttpClient client(Config config) {
        return new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    var request = chain.request();
                    var response = chain.proceed(request);
                    if (!response.isSuccessful()) {
                        switch (response.code()) {
                            case 404:
                                throw new ResourceNotFoundError("Resource not found: " + request.url());
                            case 429:
                                throw new TooManyRequestsError("Rate limit exceeded: " + request.url());
                            default:
                                throw new PokeApiError("Unexpected error: " + request.url());
                        }
                    }
                    return response;
                })
                .build();
    }

    private Retrofit retrofit(Config config) {
        return new Retrofit.Builder()
                .baseUrl(config.getString("poke-api.base-url"))
                .addConverterFactory(JacksonConverterFactory.create(new ObjectMapper()
                        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                                config.getBoolean("poke-api.parsing.fail-on-unknown-properties"))
                        .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)))
                .client(client(config))
                .build();
    }

    /**
     * Create a new {@link PokeService} instance using the factory configuration.
     * 
     * @return PokeService instance.
     */
    public PokeService create() {
        Config config = reconcileConfig();

        return new RetrofitPokeService(
                retrofit(config),
                retrier(config),
                config.getInt("poke-api.page-size"));
    }
}
