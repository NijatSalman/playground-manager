package com.company.playgroundmanager.playground.api.service;

import com.company.playgroundmanager.common.model.AttractionType;
import com.company.playgroundmanager.common.model.TicketNumber;
import com.company.playgroundmanager.playground.api.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PlaySiteMapperTest {

    private PlaySiteMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new PlaySiteMapper();
    }

    @Test
    void shouldMapPlaySiteRequestToDomain() {
        PlaySiteRequest request = new PlaySiteRequest();
        request.setName("FunZone");

        Attraction attraction1 = Attraction.builder().type(AttractionType.SLIDE).capacity(5).build();
        Attraction attraction2 = Attraction.builder().type(AttractionType.BALL_PIT).capacity(10).build();
        request.setAttractions(List.of(attraction1, attraction2));

        PlaySite result = mapper.toDomain(request);

        assertThat(result.getName()).isEqualTo("FunZone");
        assertThat(result.getAttractions()).hasSize(2);
        assertThat(result.getAttractions().get(0).getType()).isEqualTo(AttractionType.SLIDE);
        assertThat(result.getAttractions().get(1).getCapacity()).isEqualTo(10);
    }

    @Test
    void shouldMapPlaySiteToResponse() {
        Attraction attraction = Attraction.builder().type(AttractionType.SWING).capacity(6).build();
        PlaySite playSite = PlaySite.builder()
                .name("HappyLand")
                .attractions(List.of(attraction))
                .build();

        playSite.getCurrentKids().add(Kid.builder().name("John").age(5).ticketNumber(new TicketNumber("T1")).build());
        playSite.getWaitingQueue().add(Kid.builder().name("Alice").age(6).ticketNumber(new TicketNumber("T2")).build());

        PlaySiteResponse response = mapper.toResponse(playSite);

        assertThat(response.getName()).isEqualTo("HappyLand");
        assertThat(response.getAttractions()).hasSize(1);
        assertThat(response.getTotalCapacity()).isEqualTo(6);
        assertThat(response.getCurrentKidsCount()).isEqualTo(1);
        assertThat(response.getWaitingQueueSize()).isEqualTo(1);
    }
}