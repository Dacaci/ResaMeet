package com.resameet.application.service;

import com.resameet.application.exception.BusinessException;
import com.resameet.domain.model.Ressource;
import com.resameet.domain.repository.RessourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RessourceService {
    private final RessourceRepository ressourceRepository;

    public List<Ressource> getAllRessources() {
        return ressourceRepository.findAll();
    }

    public List<Ressource> searchRessources(String search) {
        if (search == null || search.trim().isEmpty()) {
            return getAllRessources();
        }
        return ressourceRepository.searchByNomOrLocalisation(search.trim());
    }

    public Ressource getRessourceById(Long id) {
        return ressourceRepository.findById(id)
            .orElseThrow(() -> new BusinessException("RESOURCE_NOT_FOUND", "Ressource non trouvée"));
    }

    @Transactional
    public Ressource createRessource(Ressource ressource) {
        return ressourceRepository.save(ressource);
    }

    @Transactional
    public Ressource updateRessource(Long id, Ressource ressource) {
        Ressource existing = getRessourceById(id);
        existing.setNom(ressource.getNom());
        existing.setCapacite(ressource.getCapacite());
        existing.setLocalisation(ressource.getLocalisation());
        existing.setDescription(ressource.getDescription());
        return ressourceRepository.save(existing);
    }

    @Transactional
    public void deleteRessource(Long id) {
        Ressource ressource = getRessourceById(id);
        ressourceRepository.delete(ressource);
    }
}
