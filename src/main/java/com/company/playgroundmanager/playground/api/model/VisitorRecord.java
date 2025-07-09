package com.company.playgroundmanager.playground.api.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VisitorRecord {

    private String ticketNumber;
    private String kidName;
    private int kidAge;
    private String playSiteName;
    private boolean inQueue;

}
