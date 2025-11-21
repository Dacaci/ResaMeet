import { Routes } from '@angular/router';
import { LoginComponent } from './features/auth/login/login.component';
import { RessourceListComponent } from './features/ressources/ressource-list/ressource-list.component';
import { RessourceDetailComponent } from './features/ressources/ressource-detail/ressource-detail.component';
import { MyReservationsComponent } from './features/reservations/my-reservations/my-reservations.component';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { 
    path: 'resources', 
    component: RessourceListComponent,
    canActivate: [authGuard]
  },
  { 
    path: 'resources/:id', 
    component: RessourceDetailComponent,
    canActivate: [authGuard]
  },
  { 
    path: 'my-reservations', 
    component: MyReservationsComponent,
    canActivate: [authGuard]
  },
  { path: '**', redirectTo: '/login' }
];
