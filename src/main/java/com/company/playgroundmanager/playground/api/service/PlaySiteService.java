package com.company.playgroundmanager.playground.api.service;

import com.company.playgroundmanager.infrastructure.persistence.InMemoryPlaySiteRepository;
import com.company.playgroundmanager.playground.api.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaySiteService {

    private final InMemoryPlaySiteRepository playSiteRepository;
    private final PlaySiteMapper playSiteMapper;

    public PlaySiteResponse createPlaySite(PlaySiteRequest request) {
        log.info("Creating play site with name: {}", request.getName());
        PlaySite playSite = playSiteMapper.toDomain(request);

        if (playSiteRepository.exists(request.getName())) {
            log.warn("Play site with name '{}' already exists", request.getName());
            throw new RuntimeException("Play site already exists with name: " + request.getName());
        }

        playSiteRepository.save(playSite);

        log.debug("Successfully created play site: {}", playSite.getName());
        return playSiteMapper.toResponse(playSite);
    }

    public PlaySiteResponse updatePlaySite(String name, PlaySiteRequest request) {
        log.info("Updating play site with name: {}", name);

        PlaySite playSite = playSiteRepository.findByName(name)
                .orElseThrow(() -> {
                    log.error("Play site not found for update with name: {}", name);
                    return new RuntimeException("Play site not found for name " + name);
                });

        playSite.setName(request.getName());
        playSite.setAttractions(playSiteMapper.toDomain(request).getAttractions());
        playSite.getCurrentKids().clear();
        playSite.getWaitingQueue().clear();

        log.debug("Successfully updated play site: {}", name);
        return playSiteMapper.toResponse(playSite);
    }

    public List<PlaySiteResponse> getAllPlaySites() {
        log.debug("Fetching all play sites ...");
        return playSiteRepository.findAll().stream()
                .map(playSiteMapper::toResponse)
                .collect(Collectors.toList());
    }

    public PlaySiteResponse getPlaySite(String name) {
        log.debug("Fetching play site with name: {}", name);
        PlaySite playSite = playSiteRepository.findByName(name)
                .orElseThrow(() -> {
                    log.error("Play site not found with name: {}", name);
                    return new RuntimeException("Play site not found for name: " + name);
                });
        return playSiteMapper.toResponse(playSite);
    }

    public void deletePlaySite(String name) {
        log.info("Deleting play site with name: {}", name);
        if (!playSiteRepository.exists(name)) {
            log.warn("Attempted to delete non-existent play site: {}", name);
            throw new RuntimeException("Play site not found for name: " + name);
        }
        playSiteRepository.delete(name);
        log.debug("Successfully deleted play site '{}'", name);
    }

    public List<PlaySiteDetailResponse> getPlaySiteDetails() {
        return playSiteRepository.findAll().stream()
                .map(site -> PlaySiteDetailResponse.builder()
                        .name(site.getName())
                        .totalCapacity(site.getTotalCapacity())
                        .currentKidCount(site.getCurrentKids().size())
                        .waitingQueueSize(site.getWaitingQueue().size())
                        .utilization(calculateUtilization(site))
                        .currentKids(site.getCurrentKids().stream()
                                .map(k -> Kid.builder()
                                        .name(k.getName())
                                        .age(k.getAge())
                                        .ticketNumber(k.getTicketNumber())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }

    private double calculateUtilization(PlaySite site) {
        int capacity = site.getTotalCapacity();
        if (capacity == 0) return 0.0;
        return (site.getCurrentKids().size() * 100.0) / capacity;
    }
}
