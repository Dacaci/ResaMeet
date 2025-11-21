package com.resameet.presentation.mapper;

import com.resameet.domain.model.Reservation;
import com.resameet.presentation.dto.ReservationDto;
import org.springframework.stereotype.Component;

@Component
public class ReservationMapper {
    public ReservationDto toDto(Reservation reservation) {
        ReservationDto dto = new ReservationDto();
        dto.setId(reservation.getId());
        dto.setResourceId(reservation.getResourceId());
        dto.setUserId(reservation.getUserId());
        dto.setStartDateTime(reservation.getStartDateTime());
        dto.setEndDateTime(reservation.getEndDateTime());
        dto.setNbParticipants(reservation.getNbParticipants());
        dto.setStatus(reservation.getStatus());
        return dto;
    }
}
