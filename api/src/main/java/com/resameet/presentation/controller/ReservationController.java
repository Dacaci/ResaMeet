package com.resameet.presentation.controller;

import com.resameet.application.service.ReservationService;
import com.resameet.domain.model.Reservation;
import com.resameet.domain.model.User;
import com.resameet.domain.repository.UserRepository;
import com.resameet.presentation.dto.ReservationDto;
import com.resameet.presentation.mapper.ReservationMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;
    private final ReservationMapper reservationMapper;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<ReservationDto> createReservation(
            @Valid @RequestBody ReservationDto dto,
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        
        Reservation reservation = reservationService.createReservation(
            userId,
            dto.getResourceId(),
            dto.getDateDebut(),
            dto.getDateFin()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(reservationMapper.toDto(reservation));
    }

    @GetMapping
    public ResponseEntity<List<ReservationDto>> getMyReservations(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        List<Reservation> reservations = reservationService.getReservationsByUser(userId);
        return ResponseEntity.ok(reservations.stream()
            .map(reservationMapper::toDto)
            .collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationDto> getReservationById(@PathVariable Long id) {
        Reservation reservation = reservationService.getReservationById(id);
        return ResponseEntity.ok(reservationMapper.toDto(reservation));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ReservationDto> cancelReservation(
            @PathVariable Long id,
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        Reservation reservation = reservationService.cancelReservation(id, userId);
        return ResponseEntity.ok(reservationMapper.toDto(reservation));
    }

    @PostMapping("/{id}/confirm")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReservationDto> confirmReservation(@PathVariable Long id) {
        Reservation reservation = reservationService.confirmReservation(id);
        return ResponseEntity.ok(reservationMapper.toDto(reservation));
    }

    @PostMapping("/{id}/refuse")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReservationDto> refuseReservation(@PathVariable Long id) {
        Reservation reservation = reservationService.refuseReservation(id);
        return ResponseEntity.ok(reservationMapper.toDto(reservation));
    }


    private Long getUserIdFromAuthentication(Authentication authentication) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        return user.getId();
    }
}
