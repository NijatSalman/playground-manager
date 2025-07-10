package com.company.playgroundmanager.infrastructure.persistence;

import com.company.playgroundmanager.playground.api.model.PlaySite;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryPlaySiteRepository {

    private final Map<String, PlaySite> store = new ConcurrentHashMap<>();

    public void save(PlaySite playSite) {
        store.put(playSite.getName(), playSite);
    }

    public Optional<PlaySite> findByName(String name) {
        return Optional.ofNullable(store.get(name));
    }

    public void delete(String name) {
        store.remove(name);
    }

    public boolean exists(String name) {
        return store.containsKey(name);
    }

    public List<PlaySite> findAll() {
        return new ArrayList<>(store.values());
    }

}
