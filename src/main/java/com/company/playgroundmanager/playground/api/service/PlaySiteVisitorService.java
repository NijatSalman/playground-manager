package com.company.playgroundmanager.playground.api.service;

import com.company.playgroundmanager.common.model.TicketNumber;
import com.company.playgroundmanager.common.config.exception.PlayGroundValidationException;
import com.company.playgroundmanager.common.config.exception.RecordNotFoundException;
import com.company.playgroundmanager.playground.api.model.VisitorRecord;
import com.company.playgroundmanager.infrastructure.persistence.InMemoryPlaySiteRepository;
import com.company.playgroundmanager.infrastructure.persistence.InMemoryPlaySiteVisitorRepository;
import com.company.playgroundmanager.playground.api.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.company.playgroundmanager.playground.api.model.PlaySiteConstant.KID_ADDED;
import static com.company.playgroundmanager.playground.api.model.PlaySiteConstant.KID_REFUSED;
import static com.company.playgroundmanager.playground.api.model.PlaySiteConstant.KID_IN_QUEUE;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaySiteVisitorService {

    private final InMemoryPlaySiteVisitorRepository visitorRepository;
    private final InMemoryPlaySiteRepository playSiteRepository;
    private final PlaySiteQueueManager playSiteQueueManager;
    private final VisitorMapper visitorMapper;

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

    public RemoveVisitorResponse removeByTicketNumber(String ticketNumber) {
        VisitorRecord record = visitorRepository.find(ticketNumber)
                .orElseThrow(() -> {
                    log.warn("No visitor found with ticket: {}", ticketNumber);
                    return new RecordNotFoundException("Visitor not found with ticket: " + ticketNumber);
                });

        PlaySite playSite = getPlaySiteOrThrow(record.getPlaySiteName());

        boolean removedFromPlaySite = removeKidFromPlaySite(playSite, ticketNumber);
        boolean removedFromQueue = !removedFromPlaySite && removeKidFromQueue(playSite, ticketNumber);

        if (!removedFromPlaySite && !removedFromQueue) {
            log.warn("Visitor with ticket '{}' was registered but not found in play site data", ticketNumber);
            throw new IllegalStateException("Visitor was registered but not present in play site data");
        }

        visitorRepository.delete(ticketNumber);

        Kid promotedKid = null;
        if (removedFromPlaySite) {
            Optional<Kid> promoted = playSiteQueueManager.promoteNextKidIfSlotAvailable(playSite);
            if (promoted.isPresent()) {
                promotedKid = promoted.get();
                visitorRepository.save(visitorMapper.toVisitorRecord(promotedKid, playSite.getName(), false));
            }
        }

        Kid removedVisitor = visitorMapper.toKid(record);

        return RemoveVisitorResponse.builder()
                .wasInQueue(record.isInQueue())
                .removedKid(removedVisitor)
                .promotedKid(promotedKid)
                .build();
    }

    public int getTotalVisitorCount() {
        return visitorRepository.findAll().size();
    }

    public Map<String, List<VisitorRecord>> getVisitorsGroupedByPlaySite() {
        return visitorRepository.findAll().values().stream()
                .collect(Collectors.groupingBy(VisitorRecord::getPlaySiteName));
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
