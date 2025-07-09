package com.company.playgroundmanager.playground.api.service;

import com.company.playgroundmanager.playground.api.model.Kid;
import com.company.playgroundmanager.playground.api.model.PlaySite;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class PlaySiteQueueManager {

    public Optional<Kid> promoteNextKidIfSlotAvailable(PlaySite playSite) {
        if (playSite.isFull() || playSite.getWaitingQueue().isEmpty()) {
            return Optional.empty();
        }

        Kid promotedKid = playSite.getWaitingQueue().poll();
        playSite.getCurrentKids().add(promotedKid);

        log.info("Promoted kid from queue to play site: {}", promotedKid.getTicketNumber().getValue());
        return Optional.of(promotedKid);
    }
}
