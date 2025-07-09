package com.company.playgroundmanager.playground.api.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PlaySiteResponse {
    private String name;
    private List<Attraction> attractions;
    private int totalCapacity;
    private int currentKidsCount;
    private int waitingQueueSize;
}
