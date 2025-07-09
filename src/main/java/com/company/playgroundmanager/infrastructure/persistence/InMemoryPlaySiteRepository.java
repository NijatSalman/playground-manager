package com.company.playgroundmanager.infrastructure.persistence;

import com.company.playgroundmanager.playground.api.model.PlaySite;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryPlaySiteRepository {

    private final Map<UUID, PlaySite> store = new ConcurrentHashMap<>();

    public void save(PlaySite playSite) {
        store.put(playSite.getId(), playSite);
    }

    public Optional<PlaySite> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    public void delete(UUID id) {
        store.remove(id);
    }

    public boolean exists(UUID id) {
        return store.containsKey(id);
    }

    public List<PlaySite> findAll() {
        return new ArrayList<>(store.values());
    }

}
