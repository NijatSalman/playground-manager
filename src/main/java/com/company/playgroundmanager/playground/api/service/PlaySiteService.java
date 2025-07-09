package com.company.playgroundmanager.playground.api.service;

import com.company.playgroundmanager.infrastructure.persistence.InMemoryPlaySiteRepository;
import com.company.playgroundmanager.playground.api.model.PlaySite;
import com.company.playgroundmanager.playground.api.model.PlaySiteRequest;
import com.company.playgroundmanager.playground.api.model.PlaySiteResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlaySiteService {

    private final InMemoryPlaySiteRepository playSiteRepository;
    private final PlaySiteMapper playSiteMapper;

    public PlaySiteResponse createPlaySite(PlaySiteRequest request) {
        UUID id = UUID.randomUUID();
        PlaySite playSite = playSiteMapper.toDomain(id, request);
        playSiteRepository.save(playSite);
        return playSiteMapper.toResponse(playSite);
    }

    public PlaySiteResponse updatePlaySite(UUID id, PlaySiteRequest request) {
        PlaySite playSite = playSiteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Play site not found for id " + id));
        playSite.setName(request.getName());
        playSite.setAttractions(playSiteMapper.toDomain(id, request).getAttractions());
        playSite.getCurrentKids().clear();
        playSite.getWaitingQueue().clear();
        return playSiteMapper.toResponse(playSite);
    }

    public PlaySiteResponse getPlaySite(UUID id) {
        PlaySite playSite = playSiteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Play site not found for id " + id));
        return playSiteMapper.toResponse(playSite);
    }

    public void deletePlaySite(UUID id) {
        if (!playSiteRepository.exists(id)) {
            throw new RuntimeException("Play site not found for id " + id);
        }
        playSiteRepository.delete(id);
    }
}
