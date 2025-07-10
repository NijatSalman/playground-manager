package com.company.playgroundmanager.playground.api.service;

import com.company.playgroundmanager.common.config.exception.PlayGroundValidationException;
import com.company.playgroundmanager.common.config.exception.RecordNotFoundException;
import com.company.playgroundmanager.common.model.AttractionType;
import com.company.playgroundmanager.common.model.TicketNumber;
import com.company.playgroundmanager.infrastructure.persistence.InMemoryPlaySiteRepository;
import com.company.playgroundmanager.infrastructure.persistence.InMemoryPlaySiteVisitorRepository;
import com.company.playgroundmanager.playground.api.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PlaySiteVisitorServiceTest {

    private PlaySiteVisitorService service;
    private InMemoryPlaySiteVisitorRepository visitorRepository;
    private InMemoryPlaySiteRepository playSiteRepository;

    @BeforeEach
    void setUp() {
        visitorRepository = new InMemoryPlaySiteVisitorRepository();
        playSiteRepository = new InMemoryPlaySiteRepository();
        service = new PlaySiteVisitorService(
                visitorRepository,
                playSiteRepository,
                new PlaySiteQueueManager(),
                new VisitorMapper()
        );
    }

    @Test
    void shouldAddKidToPlaySite() {
        PlaySite playSite = PlaySite.builder()
                .name("SiteA")
                .attractions(List.of(Attraction.builder().type(AttractionType.SLIDE).capacity(2).build()))
                .build();
        playSiteRepository.save(playSite);

        PlaySiteVisitorRequest request = new PlaySiteVisitorRequest();
        request.setAcceptQueue(false);
        request.setName("Tom");
        request.setAge(5);
        request.setTicketNumber("T123");

        String result = service.addKid("SiteA", request);
        assertThat(result).isEqualTo("Kid added to play site");
        assertThat(visitorRepository.exists("T123")).isTrue();
    }

    @Test
    void shouldAddKidToQueueIfFullAndAcceptQueueTrue() {
        PlaySite playSite = PlaySite.builder()
                .name("SiteB")
                .attractions(List.of(Attraction.builder().type(AttractionType.SLIDE).capacity(1).build()))
                .currentKids(List.of(Kid.builder().name("A").age(6).ticketNumber(new TicketNumber("T1")).build()))
                .build();
        playSiteRepository.save(playSite);

        PlaySiteVisitorRequest request = new PlaySiteVisitorRequest();
        request.setAcceptQueue(true);
        request.setName("Tom");
        request.setAge(5);
        request.setTicketNumber("T123");

        String result = service.addKid("SiteB", request);
        assertThat(result).isEqualTo("Play site is full. Kid added to waiting queue");
    }

    @Test
    void shouldRefuseKidIfFullAndQueueNotAccepted() {
        PlaySite playSite = PlaySite.builder()
                .name("SiteC")
                .attractions(List.of(Attraction.builder().type(AttractionType.SLIDE).capacity(1).build()))
                .currentKids(List.of(Kid.builder().name("A").age(6).ticketNumber(new TicketNumber("T1")).build()))
                .build();
        playSiteRepository.save(playSite);

        PlaySiteVisitorRequest request = new PlaySiteVisitorRequest();
        request.setAcceptQueue(false);
        request.setName("Tom");
        request.setAge(5);
        request.setTicketNumber("T123");

        String result = service.addKid("SiteC", request);
        assertThat(result).isEqualTo("Play site is full and kid refused to wait");
    }

    @Test
    void shouldThrowWhenTicketAlreadyUsed() {
        visitorRepository.save(VisitorRecord.builder()
                .ticketNumber("T123")
                .kidName("Zara")
                .playSiteName("SiteX")
                .build());

        PlaySite playSite = PlaySite.builder()
                .name("SiteX")
                .attractions(List.of(Attraction.builder().type(AttractionType.SLIDE).capacity(2).build()))
                .build();
        playSiteRepository.save(playSite);

        PlaySiteVisitorRequest request = new PlaySiteVisitorRequest();
        request.setAcceptQueue(true);
        request.setName("Tom");
        request.setAge(5);
        request.setTicketNumber("T123");

        assertThatThrownBy(() -> service.addKid("SiteX", request))
                .isInstanceOf(PlayGroundValidationException.class)
                .hasMessage("Ticket number already in use");
    }

    @Test
    void shouldRemoveKidAndPromoteIfQueueExists() {
        PlaySite playSite = PlaySite.builder()
                .name("PromoSite")
                .attractions(List.of(Attraction.builder().type(AttractionType.SLIDE).capacity(1).build()))
                .currentKids(new ArrayList<>(List.of(Kid.builder().name("Nijat").age(6).ticketNumber(new TicketNumber("T001")).build())))
                .build();
        playSite.getWaitingQueue().add(Kid.builder().name("New").age(5).ticketNumber(new TicketNumber("T002")).build());
        playSiteRepository.save(playSite);

        visitorRepository.save(VisitorRecord.builder()
                .ticketNumber("T001")
                .kidName("Nijat")
                .kidAge(6)
                .playSiteName("PromoSite")
                .inQueue(false)
                .build());

        RemoveVisitorResponse response = service.removeByTicketNumber("T001");
        assertThat(response.getRemovedKid().getTicketNumber().getValue()).isEqualTo("T001");
        assertNotNull(response.getPromotedKid());
        assertThat(visitorRepository.exists("T002")).isTrue();
    }

    @Test
    void shouldThrowWhenRemovingUnknownTicket() {
        assertThatThrownBy(() -> service.removeByTicketNumber("X999"))
                .isInstanceOf(RecordNotFoundException.class);
    }

    @Test
    void shouldReturnVisitorGroupedByPlaySite() {
        visitorRepository.save(VisitorRecord.builder().ticketNumber("1").playSiteName("X").build());
        visitorRepository.save(VisitorRecord.builder().ticketNumber("2").playSiteName("Y").build());
        visitorRepository.save(VisitorRecord.builder().ticketNumber("3").playSiteName("X").build());

        var grouped = service.getVisitorsGroupedByPlaySite();
        assertThat(grouped.get("X")).hasSize(2);
        assertThat(grouped.get("Y")).hasSize(1);
    }

    @Test
    void shouldReturnCorrectVisitorCount() {
        visitorRepository.save(VisitorRecord.builder().ticketNumber("1").playSiteName("X").build());
        visitorRepository.save(VisitorRecord.builder().ticketNumber("2").playSiteName("Y").build());

        assertThat(service.getTotalVisitorCount()).isEqualTo(2);
    }
}