import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { RessourceService } from '../../../core/services/ressource.service';
import { Ressource } from '../../../core/models/ressource.model';

@Component({
  selector: 'app-ressource-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  template: `
    <div>
      <h1 style="margin-bottom: 20px;">Ressources Disponibles</h1>

      <div class="form-group" style="margin-bottom: 20px;">
        <input 
          type="text" 
          placeholder="Rechercher par nom ou localisation..."
          [(ngModel)]="searchTerm"
          (input)="onSearch()"
          style="max-width: 400px;">
      </div>

      <div *ngIf="loading" style="text-align: center; padding: 40px;">
        Chargement...
      </div>

      <div *ngIf="errorMessage" class="alert alert-error">
        {{ errorMessage }}
      </div>

      <div *ngIf="!loading && !errorMessage">
        <div *ngIf="ressources.length === 0" class="card" style="text-align: center; padding: 40px;">
          Aucune ressource trouvée
        </div>

        <div *ngFor="let ressource of ressources" class="card">
          <h3>{{ ressource.nom }}</h3>
          <p><strong>Localisation:</strong> {{ ressource.localisation }}</p>
          <p><strong>Capacité:</strong> {{ ressource.capacite }} personnes</p>
          <p *ngIf="ressource.description">{{ ressource.description }}</p>
          <button 
            routerLink="/resources/{{ ressource.id }}" 
            class="btn btn-primary">
            Voir les réservations
          </button>
        </div>
      </div>
    </div>
  `,
  styles: []
})
export class RessourceListComponent implements OnInit {
  ressources: Ressource[] = [];
  loading = false;
  errorMessage = '';
  searchTerm = '';

  constructor(
    private ressourceService: RessourceService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadRessources();
  }

  loadRessources(): void {
    this.loading = true;
    this.errorMessage = '';

    this.ressourceService.getAllRessources(this.searchTerm).subscribe({
      next: (data) => {
        this.ressources = data;
        this.loading = false;
      },
      error: (error) => {
        this.errorMessage = 'Erreur lors du chargement des ressources';
        this.loading = false;
      }
    });
  }

  onSearch(): void {
    this.loadRessources();
  }
}
