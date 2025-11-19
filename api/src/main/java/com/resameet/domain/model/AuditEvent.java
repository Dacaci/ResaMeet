package com.resameet.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "audit_events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditEvent {
    @Id
    private String id;

    private String type;
    private Long userId;
    private Long resourceId;
    private Long reservationId;
    private LocalDateTime timestamp;
    private Map<String, Object> payload;
}
