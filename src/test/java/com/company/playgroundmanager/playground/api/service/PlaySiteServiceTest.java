package com.company.playgroundmanager.playground.api.service;

import com.company.playgroundmanager.common.config.exception.RecordNotFoundException;
import com.company.playgroundmanager.common.model.AttractionType;
import com.company.playgroundmanager.infrastructure.persistence.InMemoryPlaySiteRepository;
import com.company.playgroundmanager.playground.api.model.Attraction;
import com.company.playgroundmanager.playground.api.model.PlaySiteDetailResponse;
import com.company.playgroundmanager.playground.api.model.PlaySiteRequest;
import com.company.playgroundmanager.playground.api.model.PlaySiteResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PlaySiteServiceTest {

    private PlaySiteService playSiteService;

    @BeforeEach
    void setUp() {
        InMemoryPlaySiteRepository repository = new InMemoryPlaySiteRepository();
        PlaySiteMapper mapper = new PlaySiteMapper();
        playSiteService = new PlaySiteService(repository, mapper);
    }

    @Test
    void shouldCreatePlaySite() {
        PlaySiteRequest request = new PlaySiteRequest();
        request.setName("FunZone");
        request.setAttractions(List.of(Attraction.builder().type(AttractionType.SLIDE).capacity(5).build()));

        PlaySiteResponse response = playSiteService.createPlaySite(request);

        assertThat(response.getName()).isEqualTo("FunZone");
        assertThat(response.getTotalCapacity()).isEqualTo(5);
    }

    @Test
    void shouldThrowIfPlaySiteExists() {
        PlaySiteRequest request = new PlaySiteRequest();
        request.setName("CityPark");
        request.setAttractions(List.of(Attraction.builder().type(AttractionType.SWING).capacity(2).build()));

        playSiteService.createPlaySite(request);

        assertThrows(RecordNotFoundException.class, () -> playSiteService.createPlaySite(request));
    }

    @Test
    void shouldUpdatePlaySite() {
        PlaySiteRequest request = new PlaySiteRequest();
        request.setName("UpdateMe");
        request.setAttractions(List.of(Attraction.builder().type(AttractionType.SWING).capacity(3).build()));


        playSiteService.createPlaySite(request);

        PlaySiteRequest update = new PlaySiteRequest();
        update.setName("UpdatedSite");
        update.setAttractions(List.of(Attraction.builder().type(AttractionType.SLIDE).capacity(4).build()));

        PlaySiteResponse response = playSiteService.updatePlaySite("UpdateMe", update);

        assertThat(response.getName()).isEqualTo("UpdatedSite");
        assertThat(response.getTotalCapacity()).isEqualTo(4);
    }

    @Test
    void shouldThrowIfUpdatingMissingPlaySite() {
        PlaySiteRequest update =new  PlaySiteRequest();
        update.setName("UpdateMe");
        update.setAttractions(List.of(Attraction.builder().type(AttractionType.SLIDE).capacity(1).build()));

        assertThrows(RecordNotFoundException.class, () -> playSiteService.updatePlaySite("Unknown", update));
    }

    @Test
    void shouldDeletePlaySite() {
        PlaySiteRequest request = new PlaySiteRequest();
        request.setName("DeleteMe");
        request.setAttractions(List.of(Attraction.builder().type(AttractionType.SLIDE).capacity(1).build()));

        playSiteService.createPlaySite(request);

        playSiteService.deletePlaySite("DeleteMe");

        assertThrows(RecordNotFoundException.class, () -> playSiteService.getPlaySite("DeleteMe"));
    }

    @Test
    void shouldReturnAllPlaySites() {
        PlaySiteRequest playSiteRequest = new PlaySiteRequest();
        playSiteRequest.setName("A");
        playSiteRequest.setAttractions(List.of());

        PlaySiteRequest playSiteRequestB = new PlaySiteRequest();
        playSiteRequestB.setName("B");
        playSiteRequestB.setAttractions(List.of());

        playSiteService.createPlaySite(playSiteRequest);
        playSiteService.createPlaySite(playSiteRequestB);

        List<PlaySiteResponse> list = playSiteService.getAllPlaySites();

        assertThat(list).hasSize(2);
    }

    @Test
    void shouldReturnPlaySiteDetails() {
        PlaySiteRequest playSiteRequest = new PlaySiteRequest();
        playSiteRequest.setName("DetailPark");
        playSiteRequest.setAttractions(List.of(Attraction.builder().type(AttractionType.SWING).capacity(3).build()));
        playSiteService.createPlaySite(playSiteRequest);
        List<PlaySiteDetailResponse> details = playSiteService.getPlaySiteDetails();

        assertThat(details).hasSize(1);
        assertThat(details.get(0).getName()).isEqualTo("DetailPark");
        assertThat(details.get(0).getTotalCapacity()).isEqualTo(3);
    }
}