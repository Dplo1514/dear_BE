package com.sparta.hh99_actualproject.repository;

import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class EmitterRepository {

    public final Map<String, String> tokens = new ConcurrentHashMap<>();

    public String save(String id, String token) {
        tokens.put(id, token);
        return id;
    }

    public String findById(String id) {
        return tokens.get(id);
    }

    public Map<String, String> findAllStartWithById(String id) {
        return tokens.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(id))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public void deleteAllStartWithId(String id) {
        tokens.forEach(
                (key, value) -> {
                    if (key.startsWith(id)) {
                        tokens.remove(key);
                    }
                }
        );
    }

    public void deleteById(String id) {
        tokens.remove(id);
    }
}
