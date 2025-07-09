package com.company.playgroundmanager.common.model;

import lombok.Value;

@Value
public class TicketNumber {
    String value;

    public TicketNumber(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Ticket number must not be empty");
        }
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
