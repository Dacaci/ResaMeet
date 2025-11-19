package com.resameet.domain.repository;

import com.resameet.domain.model.Ressource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RessourceRepository extends JpaRepository<Ressource, Long> {
    @Query("SELECT r FROM Ressource r WHERE LOWER(r.nom) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(r.localisation) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Ressource> searchByNomOrLocalisation(@Param("search") String search);
}
