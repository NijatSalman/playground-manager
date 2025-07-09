package com.company.playgroundmanager.playground.api.model;

import lombok.Data;

import java.util.List;

@Data
public class PlaySiteRequest {
    private String name;
    private List<Attraction> attractions;
}
