package com.company.playgroundmanager.playground.api.model;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

@Data
@Builder
public class PlaySite {
    private UUID id;
    private String name;
    private List<Attraction> attractions;

    @Builder.Default
    private List<Kid> currentKids = new ArrayList<>();
    @Builder.Default
    private Queue<Kid> waitingQueue = new ConcurrentLinkedQueue<>();

    public int getTotalCapacity() {
        return attractions.stream().mapToInt(Attraction::getCapacity).sum();
    }

    public boolean isFull() {
        return currentKids.size() >= getTotalCapacity();
    }
}
