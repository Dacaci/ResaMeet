import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Reservation } from '../models/reservation.model';

@Injectable({
  providedIn: 'root'
})
export class ReservationService {
  private apiUrl = 'http://localhost:8080/api/v1';

  constructor(private http: HttpClient) {}

  createReservation(reservation: Reservation): Observable<Reservation> {
    return this.http.post<Reservation>(`${this.apiUrl}/reservations`, reservation);
  }

  getMyReservations(): Observable<Reservation[]> {
    return this.http.get<Reservation[]>(`${this.apiUrl}/reservations`);
  }

  getReservationById(id: number): Observable<Reservation> {
    return this.http.get<Reservation>(`${this.apiUrl}/reservations/${id}`);
  }

  getReservationsByResource(resourceId: number): Observable<Reservation[]> {
    return this.http.get<Reservation[]>(`${this.apiUrl}/reservations/resource/${resourceId}`);
  }

  cancelReservation(id: number): Observable<Reservation> {
    return this.http.post<Reservation>(`${this.apiUrl}/reservations/${id}/cancel`, {});
  }

  confirmReservation(id: number): Observable<Reservation> {
    return this.http.post<Reservation>(`${this.apiUrl}/reservations/${id}/confirm`, {});
  }

  refuseReservation(id: number): Observable<Reservation> {
    return this.http.post<Reservation>(`${this.apiUrl}/reservations/${id}/refuse`, {});
  }
}
