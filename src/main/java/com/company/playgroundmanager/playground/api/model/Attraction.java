package com.company.playgroundmanager.playground.api.model;

import com.company.playgroundmanager.common.model.AttractionType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Attraction {
    private AttractionType type;
    private int capacity;
}
