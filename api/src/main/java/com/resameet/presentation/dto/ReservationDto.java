package com.resameet.presentation.dto;

import com.resameet.domain.model.ReservationStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservationDto {
    private Long id;

    @NotNull(message = "L'ID de la ressource est requis")
    private Long resourceId;

    private Long userId;
    private String username;
    private String ressourceNom;

    @NotNull(message = "La date de début est requise")
    private LocalDateTime dateDebut;

    @NotNull(message = "La date de fin est requise")
    private LocalDateTime dateFin;

    private ReservationStatus status;
    private LocalDateTime dateAnnulation;
}
