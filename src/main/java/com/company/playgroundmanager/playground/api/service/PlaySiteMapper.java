package com.company.playgroundmanager.playground.api.service;

import com.company.playgroundmanager.playground.api.model.Attraction;
import com.company.playgroundmanager.playground.api.model.PlaySite;
import com.company.playgroundmanager.playground.api.model.PlaySiteRequest;
import com.company.playgroundmanager.playground.api.model.PlaySiteResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PlaySiteMapper {

    public PlaySite toDomain(PlaySiteRequest request) {
        List<Attraction> attractions = request.getAttractions().stream()
                .map(dto -> Attraction.builder()
                        .type(dto.getType())
                        .capacity(dto.getCapacity())
                        .build())
                .collect(Collectors.toList());
        return PlaySite.builder()
                .name(request.getName())
                .attractions(attractions)
                .build();
    }

    public PlaySiteResponse toResponse(PlaySite playSite) {
        List<Attraction> attractions = playSite.getAttractions().stream()
                .map(a -> Attraction.builder()
                        .type(a.getType())
                        .capacity(a.getCapacity())
                        .build())
                .collect(Collectors.toList());

        return PlaySiteResponse.builder()
                .name(playSite.getName())
                .attractions(attractions)
                .totalCapacity(playSite.getTotalCapacity())
                .currentKidsCount(playSite.getCurrentKids().size())
                .waitingQueueSize(playSite.getWaitingQueue().size())
                .build();
    }
}
