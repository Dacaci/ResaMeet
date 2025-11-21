import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReservationService } from '../../../core/services/reservation.service';
import { Reservation, ReservationStatus } from '../../../core/models/reservation.model';

@Component({
  selector: 'app-my-reservations',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div>
      <h1 style="margin-bottom: 20px;">Mes Réservations</h1>

      <div *ngIf="loading" style="text-align: center; padding: 40px;">
        Chargement...
      </div>

      <div *ngIf="errorMessage" class="alert alert-error">
        {{ errorMessage }}
      </div>

      <div *ngIf="!loading && !errorMessage">
        <div *ngIf="reservations.length === 0" class="card" style="text-align: center; padding: 40px;">
          Vous n'avez aucune réservation
        </div>

        <div *ngFor="let reservation of reservations" class="card">
          <h3>Réservation #{{ reservation.id }}</h3>
          <p><strong>Date début:</strong> {{ formatDate(reservation.startDateTime) }}</p>
          <p><strong>Date fin:</strong> {{ formatDate(reservation.endDateTime) }}</p>
          <p><strong>Participants:</strong> {{ reservation.nbParticipants }}</p>
          <p>
            <strong>Statut:</strong> 
            <span [style.color]="getStatusColor(reservation.status)">
              {{ getStatusLabel(reservation.status) }}
            </span>
          </p>
          
          <div *ngIf="canCancel(reservation)" style="margin-top: 15px;">
            <button 
              (click)="cancelReservation(reservation.id!)" 
              class="btn btn-danger"
              [disabled]="cancelling">
              {{ cancelling ? 'Annulation...' : 'Annuler' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: []
})
export class MyReservationsComponent implements OnInit {
  reservations: Reservation[] = [];
  loading = false;
  cancelling = false;
  errorMessage = '';

  constructor(private reservationService: ReservationService) {}

  ngOnInit(): void {
    this.loadReservations();
  }

  loadReservations(): void {
    this.loading = true;
    this.errorMessage = '';

    this.reservationService.getMyReservations().subscribe({
      next: (data) => {
        this.reservations = data;
        this.loading = false;
      },
      error: (error) => {
        this.errorMessage = 'Erreur lors du chargement des réservations';
        this.loading = false;
      }
    });
  }

  cancelReservation(id: number): void {
    if (!confirm('Êtes-vous sûr de vouloir annuler cette réservation ?')) {
      return;
    }

    this.cancelling = true;
    this.reservationService.cancelReservation(id).subscribe({
      next: () => {
        this.cancelling = false;
        this.loadReservations();
        alert('Réservation annulée avec succès');
      },
      error: (error) => {
        this.cancelling = false;
        const errorResponse = error.error;
        alert(errorResponse?.message || 'Erreur lors de l\'annulation');
      }
    });
  }

  canCancel(reservation: Reservation): boolean {
    return reservation.status === ReservationStatus.PENDING || 
           reservation.status === ReservationStatus.CONFIRMED;
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleString('fr-FR');
  }

  getStatusColor(status?: ReservationStatus): string {
    switch (status) {
      case ReservationStatus.CONFIRMED:
        return 'green';
      case ReservationStatus.PENDING:
        return 'orange';
      case ReservationStatus.CANCELED:
        return 'red';
      case ReservationStatus.REFUSED:
        return 'darkred';
      default:
        return 'black';
    }
  }

  getStatusLabel(status?: ReservationStatus): string {
    switch (status) {
      case ReservationStatus.CONFIRMED:
        return 'Confirmée';
      case ReservationStatus.PENDING:
        return 'En attente';
      case ReservationStatus.CANCELED:
        return 'Annulée';
      case ReservationStatus.REFUSED:
        return 'Refusée';
      default:
        return 'Inconnu';
    }
  }
}
