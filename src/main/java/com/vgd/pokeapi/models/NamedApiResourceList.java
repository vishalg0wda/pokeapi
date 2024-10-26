package com.vgd.pokeapi.models;

import java.util.List;
import java.util.Optional;

import lombok.Data;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
public class NamedApiResourceList {
    Integer count;
    String next;
    String previous;
    List<NamedApiResource> results;
    private static final Pattern OFFSET_PATTERN = Pattern.compile("offset=(\\d+)");
    
    public Optional<Integer> getNextOffset() {
        if (next == null) {
            return Optional.empty();
        }
        Matcher matcher = OFFSET_PATTERN.matcher(next);
        if (!matcher.find()) {
            return Optional.empty();
        }

        return Optional.of(Integer.parseInt(matcher.group(1)));
    }

}
