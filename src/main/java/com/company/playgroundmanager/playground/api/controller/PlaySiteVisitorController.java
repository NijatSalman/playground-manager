package com.company.playgroundmanager.playground.api.controller;

import com.company.playgroundmanager.playground.api.model.PlaySiteVisitorRequest;
import com.company.playgroundmanager.playground.api.service.PlaySiteVisitorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal-api/v1")
@RequiredArgsConstructor
public class PlaySiteVisitorController {

    private final PlaySiteVisitorService playSiteVisitorService;

    @PostMapping("/playsites/{name}/visitors")
    public ResponseEntity<String> addVisitor(@PathVariable("name") String playSiteName,
                                             @RequestBody PlaySiteVisitorRequest request) {
        String result = playSiteVisitorService.addKid(playSiteName, request);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/visitors/{ticketNumber}")
    public ResponseEntity<String> removeVisitor(@PathVariable("ticketNumber") String ticketNumber) {
        String result = playSiteVisitorService.removeByTicketNumber(ticketNumber);
        return ResponseEntity.ok(result);
    }
}
