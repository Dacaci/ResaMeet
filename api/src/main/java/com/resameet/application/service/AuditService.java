package com.resameet.application.service;

import com.resameet.domain.model.AuditEvent;
import com.resameet.domain.repository.AuditEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuditService {
    private final AuditEventRepository auditEventRepository;

    public void logEvent(String type, Long userId, Long resourceId, Long reservationId, Map<String, Object> payload) {
        AuditEvent event = new AuditEvent();
        event.setType(type);
        event.setUserId(userId);
        event.setResourceId(resourceId);
        event.setReservationId(reservationId);
        event.setTimestamp(LocalDateTime.now());
        event.setPayload(payload != null ? payload : new HashMap<>());
        auditEventRepository.save(event);
    }

    public void logReservationCreated(Long userId, Long resourceId, Long reservationId, Map<String, Object> details) {
        logEvent("RESERVATION_CREATED", userId, resourceId, reservationId, details);
    }

    public void logReservationConfirmed(Long userId, Long resourceId, Long reservationId) {
        logEvent("RESERVATION_CONFIRMED", userId, resourceId, reservationId, null);
    }

    public void logReservationCanceled(Long userId, Long resourceId, Long reservationId) {
        logEvent("RESERVATION_CANCELED", userId, resourceId, reservationId, null);
    }

    public void logReservationRefused(Long userId, Long resourceId, Long reservationId) {
        logEvent("RESERVATION_REFUSED", userId, resourceId, reservationId, null);
    }
}
