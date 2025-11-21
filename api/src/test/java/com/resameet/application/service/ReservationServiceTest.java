package com.resameet.application.service;

import com.resameet.application.exception.BusinessException;
import com.resameet.domain.model.Reservation;
import com.resameet.domain.model.ReservationStatus;
import com.resameet.domain.model.Ressource;
import com.resameet.domain.repository.ReservationRepository;
import com.resameet.domain.repository.RessourceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {
    @Mock
    private ReservationRepository reservationRepository;
    
    @Mock
    private RessourceRepository ressourceRepository;
    
    @Mock
    private AuditService auditService;
    
    @InjectMocks
    private ReservationService reservationService;
    
    private Ressource testRessource;
    private LocalDateTime futureStart;
    private LocalDateTime futureEnd;
    
    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(reservationService, "cancellationWindowHours", 2);
        
        testRessource = new Ressource();
        testRessource.setId(1L);
        testRessource.setNom("Salle A");
        testRessource.setCapacite(10);
        testRessource.setLocalisation("Bâtiment 1");
        
        futureStart = LocalDateTime.now().plusHours(1);
        futureEnd = futureStart.plusHours(2);
    }
    
    @Test
    void testCreateReservation_Success() {
        when(ressourceRepository.findById(1L)).thenReturn(Optional.of(testRessource));
        when(reservationRepository.findOverlappingReservations(any(), any(), any(), any()))
            .thenReturn(new ArrayList<>());
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> {
            Reservation r = invocation.getArgument(0);
            r.setId(1L);
            return r;
        });
        
        Reservation result = reservationService.createReservation(
            1L, 1L, futureStart, futureEnd, 5);
        
        assertNotNull(result);
        assertEquals(ReservationStatus.PENDING, result.getStatus());
        verify(reservationRepository).save(any(Reservation.class));
        verify(auditService).logReservationCreated(any(), any(), any(), any());
    }
    
    @Test
    void testCreateReservation_CapacityExceeded() {
        when(ressourceRepository.findById(1L)).thenReturn(Optional.of(testRessource));
        
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            reservationService.createReservation(1L, 1L, futureStart, futureEnd, 15);
        });
        
        assertEquals("CAPACITY_EXCEEDED", exception.getErrorCode());
    }
    
    @Test
    void testCreateReservation_Overlap() {
        when(ressourceRepository.findById(1L)).thenReturn(Optional.of(testRessource));
        
        Reservation existing = new Reservation();
        existing.setStatus(ReservationStatus.CONFIRMED);
        List<Reservation> overlapping = List.of(existing);
        when(reservationRepository.findOverlappingReservations(any(), any(), any(), any()))
            .thenReturn(overlapping);
        
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            reservationService.createReservation(1L, 1L, futureStart, futureEnd, 5);
        });
        
        assertEquals("RESERVATION_OVERLAP", exception.getErrorCode());
    }
    
    @Test
    void testCancelReservation_Success() {
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setUserId(1L);
        reservation.setResourceId(1L);
        reservation.setStartDateTime(LocalDateTime.now().plusHours(3));
        reservation.setStatus(ReservationStatus.PENDING);
        
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any())).thenReturn(reservation);
        
        Reservation result = reservationService.cancelReservation(1L, 1L);
        
        assertEquals(ReservationStatus.CANCELED, result.getStatus());
        verify(auditService).logReservationCanceled(any(), any(), any());
    }
    
    @Test
    void testCancelReservation_WindowExceeded() {
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setUserId(1L);
        reservation.setResourceId(1L);
        reservation.setStartDateTime(LocalDateTime.now().plusMinutes(30));
        reservation.setStatus(ReservationStatus.PENDING);
        
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            reservationService.cancelReservation(1L, 1L);
        });
        
        assertEquals("CANCELLATION_WINDOW_EXCEEDED", exception.getErrorCode());
    }
}
