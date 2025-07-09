package com.company.playgroundmanager.playground.api.service;

import com.company.playgroundmanager.common.model.TicketNumber;
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

    public String addKid(String playSiteName, PlaySiteVisitorRequest request) {

        if (visitorRepository.exists(request.getTicketNumber())) {
            log.warn("Ticket number '{}' is already in use", request.getTicketNumber());
            return "Ticket number already in use";
        }

        PlaySite playSite = playSiteRepository.findByName(playSiteName)
                .orElseThrow(() -> {
                    log.error("Play site '{}' not found", playSiteName);
                    return new RuntimeException("Play site not found: " + playSiteName);
                });

        Kid kid = Kid.builder()
                .name(request.getName())
                .age(request.getAge())
                .ticketNumber(new TicketNumber(request.getTicketNumber()))
                .build();

        if (playSite.getCurrentKids().contains(kid)) {
            log.warn("Kid with ticket '{}' is already in play site '{}'", request.getTicketNumber(), playSiteName);
            return "Kid already added to play site";
        }

        if (playSite.getWaitingQueue().contains(kid)) {
            log.warn("Kid with ticket '{}' is already in the waiting queue of play site '{}'", request.getTicketNumber(), playSiteName);
            return "Kid already in waiting queue";
        }

        if (!playSite.isFull()) {
            playSite.getCurrentKids().add(kid);
            log.info("Kid '{}' added to play site '{}'", request.getTicketNumber(), playSiteName);
            visitorRepository.save(VisitorRecord.builder()
                    .ticketNumber(request.getTicketNumber())
                    .kidName(request.getName())
                    .kidAge(request.getAge())
                    .playSiteName(playSiteName)
                    .inQueue(false)
                    .build());
            return "Kid added to play site";
        } else if (request.isAcceptQueue()) {
            playSite.getWaitingQueue().add(kid);
            log.info("Play site '{}' is full. Kid '{}' added to queue", playSiteName, request.getTicketNumber());
            visitorRepository.save(VisitorRecord.builder()
                    .ticketNumber(request.getTicketNumber())
                    .kidName(request.getName())
                    .kidAge(request.getAge())
                    .playSiteName(playSiteName)
                    .inQueue(true)
                    .build());
            return "Play site is full. Kid added to waiting queue";
        } else {
            log.info("Play site '{}' is full and kid '{}' refused to wait", playSiteName, request.getTicketNumber());
            return "Play site is full and kid refused to wait";
        }
    }
}
