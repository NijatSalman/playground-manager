package com.company.playgroundmanager.playground.api.controller;

import com.company.playgroundmanager.playground.api.model.PlaySiteRequest;
import com.company.playgroundmanager.playground.api.model.PlaySiteResponse;
import com.company.playgroundmanager.playground.api.service.PlaySiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(value = "/internal-api/v1/playsites")
@RequiredArgsConstructor
public class PlaySiteController {

    private final PlaySiteService playSiteService;

    @PostMapping
    public ResponseEntity<PlaySiteResponse> create(@RequestBody PlaySiteRequest request) {
        PlaySiteResponse response = playSiteService.createPlaySite(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlaySiteResponse> update(@PathVariable UUID id,
                                                   @RequestBody PlaySiteRequest request) {
        PlaySiteResponse response = playSiteService.updatePlaySite(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlaySiteResponse> get(@PathVariable UUID id) {
        PlaySiteResponse response = playSiteService.getPlaySite(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        playSiteService.deletePlaySite(id);
        return ResponseEntity.noContent().build();
    }
}
