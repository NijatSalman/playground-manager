package com.company.playgroundmanager.playground.api.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RemoveVisitorResponse {

    private Boolean wasInQueue;
    private Kid removedKid;
    private Kid promotedKid;

}
