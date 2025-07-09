package com.company.playgroundmanager.playground.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PlaySiteVisitorRequest {

    @Schema(description = "Kid's full name", example = "Alice Smith")
    private String name;
    @Schema(description = "Kid's age", example = "7")
    private int age;
    @Schema(description = "Unique ticket number for the visitor", example = "TCK-20250709-001")
    private String ticketNumber;
    @Schema(description = "If true, kid agrees to wait in queue if play site is full", example = "true")
    private boolean acceptQueue;

}
