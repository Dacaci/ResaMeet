package com.resameet.presentation.controller;

import com.resameet.application.service.RessourceService;
import com.resameet.domain.model.Ressource;
import com.resameet.presentation.dto.RessourceDto;
import com.resameet.presentation.mapper.RessourceMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/resources")
@RequiredArgsConstructor
public class RessourceController {
    private final RessourceService ressourceService;
    private final RessourceMapper ressourceMapper;

    @GetMapping
    public ResponseEntity<List<RessourceDto>> getAllRessources(
            @RequestParam(required = false) String search) {
        List<Ressource> ressources = search != null 
            ? ressourceService.searchRessources(search)
            : ressourceService.getAllRessources();
        return ResponseEntity.ok(ressources.stream()
            .map(ressourceMapper::toDto)
            .collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RessourceDto> getRessourceById(@PathVariable Long id) {
        Ressource ressource = ressourceService.getRessourceById(id);
        return ResponseEntity.ok(ressourceMapper.toDto(ressource));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RessourceDto> createRessource(@Valid @RequestBody RessourceDto dto) {
        Ressource ressource = ressourceMapper.toEntity(dto);
        Ressource created = ressourceService.createRessource(ressource);
        return ResponseEntity.status(HttpStatus.CREATED).body(ressourceMapper.toDto(created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RessourceDto> updateRessource(@PathVariable Long id,
                                                         @Valid @RequestBody RessourceDto dto) {
        Ressource ressource = ressourceMapper.toEntity(dto);
        Ressource updated = ressourceService.updateRessource(id, ressource);
        return ResponseEntity.ok(ressourceMapper.toDto(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRessource(@PathVariable Long id) {
        ressourceService.deleteRessource(id);
        return ResponseEntity.noContent().build();
    }
}
