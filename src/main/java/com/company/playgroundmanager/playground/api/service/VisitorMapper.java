package com.company.playgroundmanager.playground.api.service;

import com.company.playgroundmanager.common.model.TicketNumber;
import com.company.playgroundmanager.playground.api.model.Kid;
import com.company.playgroundmanager.playground.api.model.VisitorRecord;
import org.springframework.stereotype.Component;

@Component
public class VisitorMapper {

    public VisitorRecord toVisitorRecord(Kid kid, String playSiteName, boolean inQueue) {
        return VisitorRecord.builder()
                .ticketNumber(kid.getTicketNumber().getValue())
                .kidName(kid.getName())
                .kidAge(kid.getAge())
                .playSiteName(playSiteName)
                .inQueue(inQueue)
                .build();
    }

    public Kid toKid(VisitorRecord record) {
        return Kid.builder()
                .name(record.getKidName())
                .age(record.getKidAge())
                .ticketNumber(new TicketNumber(record.getTicketNumber()))
                .build();
    }

}
