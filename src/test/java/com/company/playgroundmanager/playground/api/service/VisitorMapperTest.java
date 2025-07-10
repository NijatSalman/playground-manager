package com.company.playgroundmanager.playground.api.service;

import com.company.playgroundmanager.common.model.TicketNumber;
import com.company.playgroundmanager.playground.api.model.Kid;
import com.company.playgroundmanager.playground.api.model.VisitorRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VisitorMapperTest {

    private VisitorMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new VisitorMapper();
    }

    @Test
    void shouldMapKidToVisitorRecord() {
        Kid kid = Kid.builder()
                .name("Alice")
                .age(7)
                .ticketNumber(new TicketNumber("TICKET123"))
                .build();

        String playSiteName = "JungleGym";
        boolean inQueue = true;

        VisitorRecord record = mapper.toVisitorRecord(kid, playSiteName, inQueue);

        assertThat(record.getTicketNumber()).isEqualTo("TICKET123");
        assertThat(record.getKidName()).isEqualTo("Alice");
        assertThat(record.getKidAge()).isEqualTo(7);
        assertThat(record.getPlaySiteName()).isEqualTo("JungleGym");
        assertThat(record.isInQueue()).isTrue();
    }

    @Test
    void shouldMapVisitorRecordToKid() {
        VisitorRecord record = VisitorRecord.builder()
                .ticketNumber("TICKET999")
                .kidName("Bob")
                .kidAge(5)
                .playSiteName("FunPark")
                .inQueue(false)
                .build();

        Kid kid = mapper.toKid(record);

        assertThat(kid.getName()).isEqualTo("Bob");
        assertThat(kid.getAge()).isEqualTo(5);
        assertThat(kid.getTicketNumber().getValue()).isEqualTo("TICKET999");
    }
}