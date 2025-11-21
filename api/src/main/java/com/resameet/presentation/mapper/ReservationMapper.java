package com.resameet.presentation.mapper;

import com.resameet.domain.model.Reservation;
import com.resameet.presentation.dto.ReservationDto;
import org.springframework.stereotype.Component;

@Component
public class ReservationMapper {
    public ReservationDto toDto(Reservation reservation) {
        ReservationDto dto = new ReservationDto();
        dto.setId(reservation.getId());
        dto.setResourceId(reservation.getRessource().getId());
        dto.setUserId(reservation.getUser().getId());
        dto.setUsername(reservation.getUser().getUsername());
        dto.setRessourceNom(reservation.getRessource().getNom());
        dto.setDateDebut(reservation.getDateDebut());
        dto.setDateFin(reservation.getDateFin());
        dto.setStatus(reservation.getStatus());
        dto.setDateAnnulation(reservation.getDateAnnulation());
        return dto;
    }
}
