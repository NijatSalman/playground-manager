package com.company.playgroundmanager.playground.api.controller;

import com.company.playgroundmanager.playground.api.model.PlaySiteRequest;
import com.company.playgroundmanager.playground.api.model.PlaySiteResponse;
import com.company.playgroundmanager.playground.api.service.PlaySiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    public ResponseEntity<List<PlaySiteResponse>> getAll() {
        List<PlaySiteResponse> response = playSiteService.getAllPlaySites();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{name}")
    public ResponseEntity<PlaySiteResponse> update(@PathVariable String name,
                                                   @RequestBody PlaySiteRequest request) {
        PlaySiteResponse response = playSiteService.updatePlaySite(name,request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{name}")
    public ResponseEntity<PlaySiteResponse> get(@PathVariable String name) {
        PlaySiteResponse response = playSiteService.getPlaySite(name);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<Void> delete(@PathVariable String name) {
        playSiteService.deletePlaySite(name);
        return ResponseEntity.noContent().build();
    }
}
