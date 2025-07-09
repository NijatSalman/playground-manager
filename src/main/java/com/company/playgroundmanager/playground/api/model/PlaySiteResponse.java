package com.company.playgroundmanager.playground.api.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class PlaySiteResponse {
    private UUID id;
    private String name;
    private List<Attraction> attractions;
    private int totalCapacity;
    private int currentKidsCount;
    private int waitingQueueSize;
}
