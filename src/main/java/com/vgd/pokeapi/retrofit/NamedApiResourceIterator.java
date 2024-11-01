
package com.vgd.pokeapi.retrofit;

import java.util.Iterator;

import com.vgd.pokeapi.models.NamedApiResource;
import com.vgd.pokeapi.models.NamedApiResourceList;

import io.github.resilience4j.retry.Retry;
import lombok.SneakyThrows;

/**
 * NamedApiResourceIterator is an iterator for iterating over NamedApiResource
 * objects
 * retrieved from the PokeApi. It handles pagination and retries for API calls.
 * 
 * @param pokeApi       The PokeApi instance used to make API calls.
 * @param namedResource The name of the resource to be retrieved.
 * @param pageSize      The number of resources to retrieve per page.
 * @param retrier       The Retry instance used to handle retries for API calls.
 */
class NamedApiResourceIterator implements Iterator<NamedApiResource> {
    private final PokeApi pokeApi;
    private final String namedResource;
    private final Integer pageSize;
    private final Retry retrier;
    private NamedApiResourceList resources;
    private Iterator<NamedApiResource> iterator;

    public NamedApiResourceIterator(PokeApi pokeApi, String namedResource, Integer pageSize, Retry retrier) {
        this.pokeApi = pokeApi;
        this.namedResource = namedResource;
        this.pageSize = pageSize;
        this.retrier = retrier;
    }

    @Override
    @SneakyThrows
    public boolean hasNext() {
        if (resources == null) {
            resources = Retry.decorateCheckedSupplier(retrier,
                    () -> pokeApi.getResourceList(namedResource, pageSize,
                            0).execute().body())
                    .get();
            iterator = resources.getResults().iterator();
        }
        if (!iterator.hasNext()) {
            if (resources.getNextOffset().isEmpty()) {
                return false;
            }
            resources = Retry.decorateCheckedSupplier(retrier,
                    () -> pokeApi.getResourceList(namedResource, pageSize, resources.getNextOffset().get()).execute()
                            .body())
                    .get();
            iterator = resources.getResults().iterator();
        }

        return iterator.hasNext();
    }

    @Override
    public NamedApiResource next() {
        return iterator.next();
    }
}