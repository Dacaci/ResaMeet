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

    @NotNull(message = "La date de début est requise")
    @Future(message = "La date de début doit être dans le futur")
    private LocalDateTime startDateTime;

    @NotNull(message = "La date de fin est requise")
    @Future(message = "La date de fin doit être dans le futur")
    private LocalDateTime endDateTime;

    @NotNull(message = "Le nombre de participants est requis")
    @Min(value = 1, message = "Le nombre de participants doit être au moins 1")
    private Integer nbParticipants;

    private ReservationStatus status;
}
