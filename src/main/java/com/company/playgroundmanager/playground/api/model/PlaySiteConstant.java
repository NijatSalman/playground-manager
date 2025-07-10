package com.company.playgroundmanager.playground.api.model;

import lombok.Getter;

@Getter
public class PlaySiteConstant {

    public static final String KID_ADDED = "Kid added to play site";
    public static final String KID_IN_QUEUE = "Play site is full. Kid added to waiting queue";
    public static final String KID_REFUSED = "Play site is full and kid refused to wait";

}
