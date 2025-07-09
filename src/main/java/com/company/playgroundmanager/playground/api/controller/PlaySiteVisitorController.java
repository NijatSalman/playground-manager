package com.company.playgroundmanager.playground.api.controller;

import com.company.playgroundmanager.playground.api.model.PlaySiteVisitorRequest;
import com.company.playgroundmanager.playground.api.service.PlaySiteVisitorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal-api/v1/playsites/{name}/visitors")
@RequiredArgsConstructor
public class PlaySiteVisitorController {

    private final PlaySiteVisitorService playSiteVisitorService;

    @PostMapping
    public ResponseEntity<String> addVisitor(@PathVariable("name") String playSiteName,
                                             @RequestBody PlaySiteVisitorRequest request) {
        String result = playSiteVisitorService.addKid(playSiteName, request);
        return ResponseEntity.ok(result);
    }
}
