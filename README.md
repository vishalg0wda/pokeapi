# PokéAPI SDK

This project offers a type-safe and ergonomic SDK to interact with [PokéAPI](https://pokeapi.co/).

## Pre-requisites

1. [Java 21](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)

## Usage


### Building the SDK

To build the SDK, run the following command in the root directory of the project:

```sh
./mvnw clean install
```

This will compile the SDK and install it into your local Maven repository.

### Adding the SDK as a Dependency

Since the SDK is not published to Maven Central, you need to add it as a local dependency in your project. Add the following to your `pom.xml`:

```xml
<dependency>
    <groupId>com.vgd.pokeapi</groupId>
    <artifactId>sdk</artifactId>
    <version>1.0-SNAPSHOT</version>
    <scope>compile</scope>
</dependency>
```

```java
import com.vgd.pokeapi.models.Generation;
import com.vgd.pokeapi.models.Pokemon;
import com.vgd.pokeapi.service.PokeService;
import com.vgd.pokeapi.service.PokeServiceFactory;


// Instantiate the SDK. It is meant to be used as a singleton and 
// offers stateless APIs making it thread-safe.
PokeService pokeService = PokeServiceFactory.instance().create();

// Get names of the first 100 pokémons
List<String> pokemonNames = pokeservice.getPokemons()
    .limit(100)
    .map(pokemon -> pokemon.getName())
    .collect(Collectors.toList());
// Get pokémon by name
Pokemon pikachu = pokeService.getPokemon("pikachu");
// Get pokémon by id
Pokemon pikachu = pokeService.getPokemon(42);

// Same applies for Generation
```

### Error Handling

Network errors that manifest as `IOException`s(or any other unhandled errors for that matter)
escalate naturally.

Non-2XX responses codes are mapped to custom errors like:
- `TooManyRequestError(statuscode: 429)`
- `ResourceNotFoundError(statuscode: 404)`
- `PokeApiError(statuscode:<any>)`

A retry policy has been  configured to retry for all errors except `ResourceNotFoundError` for
which retries are futile anyway. Given that we are calling idempotent endpoints, this should 
be feasible. 
We make 3 attempts with a 250ms wait duration between each attempt.

## Testing

```sh
./mvnw test
```

All logs will be written into `test-debug.log` in the project root directory.

## Configuration

All configuration for the SDK is injected via a `Config` type. The `Config` type is part of the 
[`lightbend/config`](https://github.com/lightbend/config) configuration framework. 

Sensible defaults for the SDK have been defined in `poke-sdk.conf` which will already be embedded into 
the JAR. 
```conf
poke-api {
  base-url = "https://pokeapi.co/api/v2/"
  
  parsing.fail-on-unknown-properties = false

  page-size = 20

  retry {
    max-attempts = 4
    wait-duration = 250ms
  }
}
```

These settings can be overridden like so:
```java
// Source overrides from a .conf file in the classpath
Config overrides = ConfigFactory.load("sdk-overrides.conf");

// Define overrides in code
Config overrides = ConfigFactory.parseString("pokeapi.retry.max-attempts = 3");

// For other ways to instantiate Config objects, see the Typesafe Config documentation:
// https://github.com/lightbend/config/blob/master/README.md

// Create a PokeService instance with the overrides
// The factory ensures that the override config is given precedence over the default config.
PokeService pokeService = PokeServiceFactory.instance()
        .withOverrides(overrides)
        .create();

```

## Design Choices

1. Provide a [Java `Stream` API](https://www.oracle.com/technical-resources/articles/java/ma14-java-se-8-streams.html) 
for accessing collections of resources. We feel Java Streams are a more conveient abstraction as they encapsulate away 
concerns of pagination and lazy-loading. The stream serves as a stateful iterator for the resource collection rather.

2. Transparently fetch resources when working with resource lists: PokeAPI's paginated results 
[only contain references](https://pokeapi.co/docs/v2#namedapiresource) to the underlying resources that need to be 
fetched individually. We feel this is an API design detail that is best hidden from users. Which is why, each
resource is automatically looked up on demand(when consuming the stream). An optimization that can be explored here
would be to prefetch resources and avoid introducing delays while streaming.

3. Resilient API calls through configurable retries.


## Techonology Choices

1. [Retrofit](https://square.github.io/retrofit/): type-safe clients for interacting with HTTP APIs. I've used it
primarily because of it's ergonomics in building out HTTP clients.

2. [lightbend/config](https://github.com/lightbend/config): 
    - Provides type-safe APIs for reading configuration.
    - Built-in mechanism to override configuration in various ways without needing to rebuild the source.
    - Expressiveness(HOCON syntax, support for referencing, substitution, lazy resolution).

3. [resilience4j-retry](https://resilience4j.readme.io/docs/retry): Provides functional APIs for applying retries.

5. [Slf4j-API](https://www.slf4j.org/): The Simple Logging Facade for Java (SLF4J) serves as a simple facade for various
logging frameworks allowing the end user to plug in the desired logging framework at deployment time. This makes it an
ideal choice for SDK/library authors

