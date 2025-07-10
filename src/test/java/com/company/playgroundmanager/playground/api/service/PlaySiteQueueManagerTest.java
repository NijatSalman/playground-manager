package com.company.playgroundmanager.playground.api.service;

import com.company.playgroundmanager.common.model.AttractionType;
import com.company.playgroundmanager.common.model.TicketNumber;
import com.company.playgroundmanager.playground.api.model.Attraction;
import com.company.playgroundmanager.playground.api.model.Kid;
import com.company.playgroundmanager.playground.api.model.PlaySite;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class PlaySiteQueueManagerTest {

    private PlaySiteQueueManager queueManager;

    @BeforeEach
    void setUp() {
        queueManager = new PlaySiteQueueManager();
    }

    @Test
    void shouldPromoteKidIfSlotAvailableAndQueueNotEmpty() {
        // given
        Kid waitingKid = Kid.builder()
                .name("Charlie")
                .age(6)
                .ticketNumber(new TicketNumber("TICK123"))
                .build();

        PlaySite playSite = PlaySite.builder()
                .name("FunZone")
                .attractions(List.of(
                        Attraction.builder().type(AttractionType.SLIDE).capacity(2).build()
                ))
                .currentKids(new ArrayList<>(List.of(
                        Kid.builder().name("Alice").age(5).ticketNumber(new TicketNumber("T1")).build()
                )))
                .build();

        playSite.getWaitingQueue().add(waitingKid);

        // when
        Optional<Kid> promoted = queueManager.promoteNextKidIfSlotAvailable(playSite);

        // then
        assertThat(promoted).isPresent();
        assertThat(promoted.get().getTicketNumber().getValue()).isEqualTo("TICK123");
        assertThat(playSite.getCurrentKids()).contains(waitingKid);
        assertThat(playSite.getWaitingQueue()).isEmpty();
    }

    @Test
    void shouldNotPromoteWhenPlaySiteIsFull() {
        // given
        PlaySite playSite = PlaySite.builder()
                .name("FullZone")
                .attractions(List.of(
                        Attraction.builder().type(AttractionType.SLIDE).capacity(1).build()
                ))
                .currentKids(List.of(
                        Kid.builder().name("Bob").age(4).ticketNumber(new TicketNumber("T2")).build()
                ))
                .build();

        playSite.getWaitingQueue().add(Kid.builder()
                .name("Daisy")
                .age(5)
                .ticketNumber(new TicketNumber("T3"))
                .build());

        // when
        Optional<Kid> promoted = queueManager.promoteNextKidIfSlotAvailable(playSite);

        // then
        assertThat(promoted).isEmpty();
        assertThat(playSite.getWaitingQueue()).hasSize(1);
    }

    @Test
    void shouldNotPromoteWhenQueueIsEmpty() {
        // given
        PlaySite playSite = PlaySite.builder()
                .name("EmptyQueuePark")
                .attractions(List.of(
                        Attraction.builder().type(AttractionType.SWING).capacity(3).build()
                ))
                .currentKids(List.of(
                        Kid.builder().name("Liam").age(6).ticketNumber(new TicketNumber("T4")).build()
                ))
                .build();

        // when
        Optional<Kid> promoted = queueManager.promoteNextKidIfSlotAvailable(playSite);

        // then
        assertThat(promoted).isEmpty();
        assertThat(playSite.getCurrentKids()).hasSize(1);
    }
}