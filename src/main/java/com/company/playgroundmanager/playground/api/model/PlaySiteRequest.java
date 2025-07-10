package com.company.playgroundmanager.playground.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Request payload to create or update a play site with its name and attractions")
public class PlaySiteRequest {

    @Schema(description = "Unique name of the play site", example = "Tallinn Park", required = true)
    private String name;

    @Schema(description = "List of attractions available in the play site", required = true)
    private List<Attraction> attractions;
}
