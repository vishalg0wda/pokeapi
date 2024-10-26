package com.vgd.pokeapi.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties({ "past_abilities" })
public class Pokemon {
    Integer id;
    String name;
    Integer baseExperience;
    Integer height;
    Boolean isDefault;
    Integer order;
    Integer weight;

    @Data
    static class PokemonAbility {
        Boolean isHidden;
        Integer slot;
        NamedApiResource ability;
    }

    List<PokemonAbility> abilities;
    List<NamedApiResource> forms;

    @Data
    static class VersionGameIndex {
        Integer gameIndex;
        NamedApiResource version;
    }

    List<VersionGameIndex> gameIndices;

    @Data
    static class PokemonHeldItem {
        @Data
        static class PokemonHeldItemVersion {
            NamedApiResource version;
            Integer rarity;
        }

        NamedApiResource item;
        List<PokemonHeldItemVersion> versionDetails;
    }

    List<PokemonHeldItem> heldItems;

    String locationAreaEncounters;

    @Data
    static class PokemonMove {
        @Data
        static class PokemonMoveVersion {
            NamedApiResource versionGroup;
            NamedApiResource moveLearnMethod;
            Integer levelLearnedAt;
        }

        NamedApiResource move;
        List<PokemonMoveVersion> versionGroupDetails;
    }

    List<PokemonMove> moves;

    @Data
    static class PokemonType {
        Integer slot;
        NamedApiResource type;
    }

    @Data
    static class PokemonTypePast {
        NamedApiResource generation;
        List<PokemonType> types;
    }

    List<PokemonTypePast> pastTypes;

    @Data
    @JsonIgnoreProperties({ "other", "versions" })
    static class PokemonSprites {
        String frontDefault;
        String frontShiny;
        String frontFemale;
        String frontShinyFemale;
        String backDefault;
        String backShiny;
        String backFemale;
        String backShinyFemale;
    }

    PokemonSprites sprites;

    @Data
    static class PokemonCries {
        String latest;
        String legacy;
    }

    PokemonCries cries;

    NamedApiResource species;

    @Data
    static class PokemonStat {
        NamedApiResource stat;
        Integer effort;
        Integer baseStat;
    }

    List<PokemonStat> stats;
    List<PokemonType> types;
}
