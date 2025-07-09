package com.company.playgroundmanager.playground.api.model;

import com.company.playgroundmanager.common.model.TicketNumber;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Kid {
    private String name;
    private int age;
    private TicketNumber ticketNumber;
}
