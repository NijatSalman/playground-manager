package com.company.playgroundmanager.playground.api.service;

import com.company.playgroundmanager.common.model.TicketNumber;
import com.company.playgroundmanager.common.model.config.exception.PlayGroundValidationException;
import com.company.playgroundmanager.common.model.config.exception.RecordNotFoundException;
import com.company.playgroundmanager.infrastructure.persistence.InMemoryPlaySiteRepository;
import com.company.playgroundmanager.infrastructure.persistence.InMemoryPlaySiteVisitorRepository;
import com.company.playgroundmanager.playground.api.model.Kid;
import com.company.playgroundmanager.playground.api.model.PlaySite;
import com.company.playgroundmanager.playground.api.model.PlaySiteVisitorRequest;
import com.company.playgroundmanager.playground.api.model.VisitorRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class PlaySiteVisitorService {

    private final InMemoryPlaySiteVisitorRepository visitorRepository;
    private final InMemoryPlaySiteRepository playSiteRepository;

    private static final String REMOVED_FROM_SITE = "Removed from play site";
    private static final String REMOVED_FROM_QUEUE = "Removed from waiting queue";
    private static final String KID_ADDED = "Kid added to play site";
    private static final String KID_IN_QUEUE = "Play site is full. Kid added to waiting queue";
    private static final String KID_REFUSED = "Play site is full and kid refused to wait";

    public String addKid(String playSiteName, PlaySiteVisitorRequest request) {
        validateTicketUniqueness(request.getTicketNumber());

        PlaySite playSite = getPlaySiteOrThrow(playSiteName);

        Kid kid = Kid.builder()
                .name(request.getName())
                .age(request.getAge())
                .ticketNumber(new TicketNumber(request.getTicketNumber()))
                .build();

        if (playSite.getCurrentKids().contains(kid)) {
            log.warn("Kid with ticket '{}' is already in play site '{}'", request.getTicketNumber(), playSiteName);
            throw new PlayGroundValidationException("Kid already added to play site");
        }

        if (playSite.getWaitingQueue().contains(kid)) {
            log.warn("Kid with ticket '{}' is already in the waiting queue of play site '{}'", request.getTicketNumber(), playSiteName);
            throw new PlayGroundValidationException("Kid already in waiting queue");
        }

        validateKidNotAlreadyExists(playSite, kid, playSiteName);

        return registerKid(playSite, kid, request, playSiteName);
    }

    public String removeByTicketNumber(String ticketNumber) {
        VisitorRecord record = visitorRepository.find(ticketNumber)
                .orElseThrow(() -> {
                    log.warn("No visitor found with ticket: {}", ticketNumber);
                    return new RecordNotFoundException(ticketNumber);
                });

        PlaySite playSite = getPlaySiteOrThrow(record.getPlaySiteName());

        boolean wasVisitorRemoved = removeKidFromPlaySite(playSite, ticketNumber)
                || removeKidFromQueue(playSite, ticketNumber);

        if (wasVisitorRemoved) {
            visitorRepository.delete(ticketNumber);
            return record.isInQueue() ? REMOVED_FROM_QUEUE : REMOVED_FROM_SITE;
        }

        log.warn("Visitor with ticket '{}' was registered but not found in play site data", ticketNumber);
        throw new IllegalStateException("Visitor was registered but not present in play site data");
    }


    private void validateKidNotAlreadyExists(PlaySite playSite, Kid kid, String siteName) {
        if (playSite.getCurrentKids().contains(kid)) {
            log.warn("Kid with ticket '{}' is already in play site '{}'", kid.getTicketNumber(), siteName);
            throw new PlayGroundValidationException("Kid already added to play site");
        }

        if (playSite.getWaitingQueue().contains(kid)) {
            log.warn("Kid with ticket '{}' is already in waiting queue of '{}'", kid.getTicketNumber(), siteName);
            throw new PlayGroundValidationException("Kid already in waiting queue");
        }
    }

    private String registerKid(PlaySite site, Kid kid, PlaySiteVisitorRequest request, String siteName) {
        if (!site.isFull()) {
            site.getCurrentKids().add(kid);
            log.debug("Kid '{}' added to play site '{}'", kid.getTicketNumber(), siteName);
            saveVisitor(request, siteName, false);
            return KID_ADDED;
        }

        if (request.isAcceptQueue()) {
            site.getWaitingQueue().add(kid);
            log.debug("Play site '{}' is full. Kid '{}' added to queue", siteName, kid.getTicketNumber());
            saveVisitor(request, siteName, true);
            return KID_IN_QUEUE;
        }

        log.debug("Play site '{}' is full and kid '{}' refused to wait", siteName, kid.getTicketNumber());
        return KID_REFUSED;
    }

    private void validateTicketUniqueness(String ticketNumber) {
        if (visitorRepository.exists(ticketNumber)) {
            log.warn("Ticket number '{}' is already in use", ticketNumber);
            throw new PlayGroundValidationException("Ticket number already in use");
        }
    }

    private PlaySite getPlaySiteOrThrow(String name) {
        return playSiteRepository.findByName(name)
                .orElseThrow(() -> {
                    log.error("Play site '{}' not found", name);
                    return new RecordNotFoundException(name);
                });
    }

    private void saveVisitor(PlaySiteVisitorRequest request, String playSiteName, boolean inQueue) {
        visitorRepository.save(VisitorRecord.builder()
                .ticketNumber(request.getTicketNumber())
                .kidName(request.getName())
                .kidAge(request.getAge())
                .playSiteName(playSiteName)
                .inQueue(inQueue)
                .build());
    }

    private boolean removeKidFromPlaySite(PlaySite playSite, String ticketNumber) {
        return playSite.getCurrentKids().removeIf(
                kid -> kid.getTicketNumber().getValue().equals(ticketNumber));
    }

    private boolean removeKidFromQueue(PlaySite playSite, String ticketNumber) {
        return playSite.getWaitingQueue().removeIf(
                kid -> kid.getTicketNumber().getValue().equals(ticketNumber));
    }
}
