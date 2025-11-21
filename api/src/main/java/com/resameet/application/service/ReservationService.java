package com.resameet.application.service;

import com.resameet.application.exception.BusinessException;
import com.resameet.domain.model.Reservation;
import com.resameet.domain.model.ReservationStatus;
import com.resameet.domain.model.Ressource;
import com.resameet.domain.model.User;
import com.resameet.domain.repository.ReservationRepository;
import com.resameet.domain.repository.RessourceRepository;
import com.resameet.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final RessourceRepository ressourceRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;

    @Value("${reservation.cancellation-window-hours:2}")
    private int cancellationWindowHours;

    @Transactional
    public Reservation createReservation(Long userId, Long resourceId, LocalDateTime dateDebut,
                                        LocalDateTime dateFin) {
        if (dateFin.isBefore(dateDebut) || dateFin.isEqual(dateDebut)) {
            throw new BusinessException("INVALID_DATE_RANGE", "La date de fin doit être après la date de début");
        }

        if (dateDebut.isBefore(LocalDateTime.now())) {
            throw new BusinessException("PAST_DATE", "La date de début doit être dans le futur");
        }

        Ressource ressource = ressourceRepository.findById(resourceId)
            .orElseThrow(() -> new BusinessException("RESOURCE_NOT_FOUND", "Ressource non trouvée"));

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "Utilisateur non trouvé"));

        List<Reservation> overlapping = reservationRepository.findOverlappingReservations(
            resourceId, dateDebut, dateFin);
        
        if (!overlapping.isEmpty()) {
            throw new BusinessException("RESERVATION_OVERLAP",
                "Une réservation existe déjà sur ce créneau horaire");
        }

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setRessource(ressource);
        reservation.setDateDebut(dateDebut);
        reservation.setDateFin(dateFin);
        reservation.setStatus(ReservationStatus.EN_ATTENTE);

        Reservation saved = reservationRepository.save(reservation);

        Map<String, Object> details = new HashMap<>();
        details.put("dateDebut", dateDebut.toString());
        details.put("dateFin", dateFin.toString());
        auditService.logReservationCreated(userId, resourceId, saved.getId(), details);

        return saved;
    }

    @Transactional
    public Reservation cancelReservation(Long reservationId, Long userId) {
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new BusinessException("RESERVATION_NOT_FOUND", "Réservation non trouvée"));

        if (!reservation.getUser().getId().equals(userId)) {
            throw new BusinessException("UNAUTHORIZED", "Vous n'êtes pas autorisé à annuler cette réservation");
        }

        if (reservation.getStatus() == ReservationStatus.ANNULEE) {
            throw new BusinessException("ALREADY_CANCELED", "La réservation est déjà annulée");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime cancellationDeadline = reservation.getDateDebut().minusHours(cancellationWindowHours);
        
        if (now.isAfter(cancellationDeadline)) {
            throw new BusinessException("CANCELLATION_WINDOW_EXCEEDED",
                String.format("Impossible d'annuler moins de %d heures avant le début de la réservation",
                    cancellationWindowHours));
        }

        reservation.setStatus(ReservationStatus.ANNULEE);
        reservation.setDateAnnulation(now);
        Reservation saved = reservationRepository.save(reservation);

        auditService.logReservationCanceled(userId, reservation.getRessource().getId(), reservationId);

        return saved;
    }

    @Transactional
    public Reservation confirmReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new BusinessException("RESERVATION_NOT_FOUND", "Réservation non trouvée"));

        if (reservation.getStatus() != ReservationStatus.EN_ATTENTE) {
            throw new BusinessException("INVALID_STATUS", "Seules les réservations en attente peuvent être confirmées");
        }

        reservation.setStatus(ReservationStatus.CONFIRMEE);
        Reservation saved = reservationRepository.save(reservation);

        auditService.logReservationConfirmed(reservation.getUser().getId(), reservation.getRessource().getId(), reservationId);

        return saved;
    }

    @Transactional
    public Reservation refuseReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new BusinessException("RESERVATION_NOT_FOUND", "Réservation non trouvée"));

        if (reservation.getStatus() != ReservationStatus.EN_ATTENTE) {
            throw new BusinessException("INVALID_STATUS", "Seules les réservations en attente peuvent être refusées");
        }

        reservation.setStatus(ReservationStatus.REFUSEE);
        Reservation saved = reservationRepository.save(reservation);

        auditService.logReservationRefused(reservation.getUser().getId(), reservation.getRessource().getId(), reservationId);

        return saved;
    }

    public List<Reservation> getReservationsByUser(Long userId) {
        return reservationRepository.findByUserId(userId);
    }

    public Reservation getReservationById(Long id) {
        return reservationRepository.findById(id)
            .orElseThrow(() -> new BusinessException("RESERVATION_NOT_FOUND", "Réservation non trouvée"));
    }
}
