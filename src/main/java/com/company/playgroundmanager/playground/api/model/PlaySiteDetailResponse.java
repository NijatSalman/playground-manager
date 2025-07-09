package com.company.playgroundmanager.playground.api.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PlaySiteDetailResponse {

    private String name;
    private int totalCapacity;
    private int currentKidCount;
    private int waitingQueueSize;
    private double utilization;
    private List<Kid> currentKids;

}
