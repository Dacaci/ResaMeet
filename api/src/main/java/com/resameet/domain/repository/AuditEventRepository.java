package com.resameet.domain.repository;

import com.resameet.domain.model.AuditEvent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditEventRepository extends MongoRepository<AuditEvent, String> {
    List<AuditEvent> findByUserId(Long userId);
    List<AuditEvent> findByResourceId(Long resourceId);
    List<AuditEvent> findByReservationId(Long reservationId);
}
