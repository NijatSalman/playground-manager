package com.company.playgroundmanager.playground.api.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CustomErrorResponse {
    private int status;
    private String message;
}
