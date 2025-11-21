import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <nav style="background-color: #007bff; color: white; padding: 15px 0;">
      <div class="container" style="display: flex; justify-content: space-between; align-items: center;">
        <div>
          <a routerLink="/resources" style="color: white; text-decoration: none; font-size: 20px; font-weight: bold;">
            ResaMeet
          </a>
        </div>
        <div style="display: flex; gap: 20px; align-items: center;">
          <ng-container *ngIf="authService.isAuthenticated(); else notAuthenticated">
            <span>{{ authService.getUser()?.username }}</span>
            <a routerLink="/resources" style="color: white; text-decoration: none;">Ressources</a>
            <a routerLink="/my-reservations" style="color: white; text-decoration: none;">Mes Réservations</a>
            <button (click)="logout()" class="btn btn-secondary" style="padding: 5px 15px;">Déconnexion</button>
          </ng-container>
          <ng-template #notAuthenticated>
            <a routerLink="/login" style="color: white; text-decoration: none;">Connexion</a>
          </ng-template>
        </div>
      </div>
    </nav>
  `,
  styles: []
})
export class NavbarComponent {
  constructor(public authService: AuthService, private router: Router) {}

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
