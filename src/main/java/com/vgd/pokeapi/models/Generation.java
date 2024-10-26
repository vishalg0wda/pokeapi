package com.vgd.pokeapi.models;

import java.util.List;

import lombok.Data;

@Data
public class Generation {
    Integer id;
    String name;
    List<NamedApiResource> abilities;
    List<Name> names;
    NamedApiResource mainRegion;
    List<NamedApiResource> moves;
    List<NamedApiResource> pokemonSpecies;
    List<NamedApiResource> types;
    List<NamedApiResource> versionGroups;
}
