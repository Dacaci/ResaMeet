package com.resameet.domain.repository;

import com.resameet.domain.model.Reservation;
import com.resameet.domain.model.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUserId(Long userId);
    
    @Query("SELECT r FROM Reservation r WHERE r.ressource.id = :ressourceId " +
           "AND r.status != 'ANNULEE' " +
           "AND ((r.dateDebut <= :dateFin AND r.dateFin >= :dateDebut))")
    List<Reservation> findOverlappingReservations(
        @Param("ressourceId") Long ressourceId,
        @Param("dateDebut") LocalDateTime dateDebut,
        @Param("dateFin") LocalDateTime dateFin
    );
    
    List<Reservation> findByRessourceId(Long ressourceId);
}
