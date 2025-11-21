import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RessourceService } from '../../../core/services/ressource.service';
import { ReservationService } from '../../../core/services/reservation.service';
import { Ressource } from '../../../core/models/ressource.model';
import { Reservation, ReservationStatus } from '../../../core/models/reservation.model';

@Component({
  selector: 'app-ressource-detail',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div>
      <button (click)="goBack()" class="btn btn-secondary" style="margin-bottom: 20px;">
        ← Retour
      </button>

      <div *ngIf="loading" style="text-align: center; padding: 40px;">
        Chargement...
      </div>

      <div *ngIf="errorMessage" class="alert alert-error">
        {{ errorMessage }}
      </div>

      <div *ngIf="!loading && ressource">
        <div class="card">
          <h2>{{ ressource.nom }}</h2>
          <p><strong>Localisation:</strong> {{ ressource.localisation }}</p>
          <p><strong>Capacité:</strong> {{ ressource.capacite }} personnes</p>
          <p *ngIf="ressource.description">{{ ressource.description }}</p>
        </div>

        <div class="card">
          <h3>Réservations existantes</h3>
          <div *ngIf="reservations.length === 0" style="padding: 20px; text-align: center; color: #666;">
            Aucune réservation
          </div>
          <table *ngIf="reservations.length > 0" class="table">
            <thead>
              <tr>
                <th>Date début</th>
                <th>Date fin</th>
                <th>Participants</th>
                <th>Statut</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let res of reservations">
                <td>{{ formatDate(res.startDateTime) }}</td>
                <td>{{ formatDate(res.endDateTime) }}</td>
                <td>{{ res.nbParticipants }}</td>
                <td>
                  <span [style.color]="getStatusColor(res.status)">
                    {{ res.status }}
                  </span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <div class="card">
          <h3>Nouvelle réservation</h3>
          <form [formGroup]="reservationForm" (ngSubmit)="onSubmit()">
            <div class="form-group">
              <label for="startDateTime">Date et heure de début</label>
              <input 
                id="startDateTime" 
                type="datetime-local" 
                formControlName="startDateTime"
                required>
            </div>

            <div class="form-group">
              <label for="endDateTime">Date et heure de fin</label>
              <input 
                id="endDateTime" 
                type="datetime-local" 
                formControlName="endDateTime"
                required>
            </div>

            <div class="form-group">
              <label for="nbParticipants">Nombre de participants</label>
              <input 
                id="nbParticipants" 
                type="number" 
                formControlName="nbParticipants"
                [max]="ressource.capacite"
                min="1"
                required>
              <small style="color: #666;">Maximum: {{ ressource.capacite }} personnes</small>
            </div>

            <div *ngIf="formError" class="alert alert-error">
              {{ formError }}
            </div>

            <button 
              type="submit" 
              class="btn btn-primary"
              [disabled]="reservationForm.invalid || submitting">
              {{ submitting ? 'Création...' : 'Créer la réservation' }}
            </button>
          </form>
        </div>
      </div>
    </div>
  `,
  styles: []
})
export class RessourceDetailComponent implements OnInit {
  ressource: Ressource | null = null;
  reservations: Reservation[] = [];
  reservationForm: FormGroup;
  loading = false;
  submitting = false;
  errorMessage = '';
  formError = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private ressourceService: RessourceService,
    private reservationService: ReservationService,
    private fb: FormBuilder
  ) {
    this.reservationForm = this.fb.group({
      startDateTime: ['', Validators.required],
      endDateTime: ['', Validators.required],
      nbParticipants: [1, [Validators.required, Validators.min(1)]]
    });
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadRessource(Number(id));
      this.loadReservations(Number(id));
    }
  }

  loadRessource(id: number): void {
    this.loading = true;
    this.ressourceService.getRessourceById(id).subscribe({
      next: (data) => {
        this.ressource = data;
        this.loading = false;
      },
      error: (error) => {
        this.errorMessage = 'Erreur lors du chargement de la ressource';
        this.loading = false;
      }
    });
  }

  loadReservations(resourceId: number): void {
    this.reservationService.getReservationsByResource(resourceId).subscribe({
      next: (data) => {
        this.reservations = data;
      },
      error: (error) => {
        console.error('Erreur lors du chargement des réservations', error);
      }
    });
  }

  onSubmit(): void {
    if (this.reservationForm.valid && this.ressource) {
      this.submitting = true;
      this.formError = '';

      const formValue = this.reservationForm.value;
      const reservation: Reservation = {
        resourceId: this.ressource.id,
        startDateTime: new Date(formValue.startDateTime).toISOString(),
        endDateTime: new Date(formValue.endDateTime).toISOString(),
        nbParticipants: formValue.nbParticipants
      };

      this.reservationService.createReservation(reservation).subscribe({
        next: () => {
          this.submitting = false;
          this.reservationForm.reset();
          this.loadReservations(this.ressource!.id);
          alert('Réservation créée avec succès !');
        },
        error: (error) => {
          this.submitting = false;
          const errorResponse = error.error;
          if (errorResponse?.code) {
            this.formError = this.getErrorMessage(errorResponse.code, errorResponse.message);
          } else {
            this.formError = 'Erreur lors de la création de la réservation';
          }
        }
      });
    }
  }

  getErrorMessage(code: string, message: string): string {
    switch (code) {
      case 'RESERVATION_OVERLAP':
        return 'Une réservation confirmée existe déjà sur ce créneau horaire';
      case 'CAPACITY_EXCEEDED':
        return 'Le nombre de participants dépasse la capacité de la ressource';
      case 'CANCELLATION_WINDOW_EXCEEDED':
        return 'Impossible d\'annuler moins de 2 heures avant le début';
      default:
        return message || 'Erreur inconnue';
    }
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

  goBack(): void {
    this.router.navigate(['/resources']);
  }
}
