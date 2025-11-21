package com.resameet.presentation.mapper;

import com.resameet.domain.model.Ressource;
import com.resameet.presentation.dto.RessourceDto;
import org.springframework.stereotype.Component;

@Component
public class RessourceMapper {
    public RessourceDto toDto(Ressource ressource) {
        RessourceDto dto = new RessourceDto();
        dto.setId(ressource.getId());
        dto.setNom(ressource.getNom());
        dto.setCapacite(ressource.getCapacite());
        dto.setLocalisation(ressource.getLocalisation());
        dto.setDescription(ressource.getDescription());
        return dto;
    }

    public Ressource toEntity(RessourceDto dto) {
        Ressource ressource = new Ressource();
        ressource.setId(dto.getId());
        ressource.setNom(dto.getNom());
        ressource.setCapacite(dto.getCapacite());
        ressource.setLocalisation(dto.getLocalisation());
        ressource.setDescription(dto.getDescription());
        return ressource;
    }
}
