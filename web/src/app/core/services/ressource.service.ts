import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Ressource } from '../models/ressource.model';

@Injectable({
  providedIn: 'root'
})
export class RessourceService {
  private apiUrl = 'http://localhost:8080/api/v1';

  constructor(private http: HttpClient) {}

  getAllRessources(search?: string): Observable<Ressource[]> {
    const params = search ? { search } : {};
    return this.http.get<Ressource[]>(`${this.apiUrl}/resources`, { params });
  }

  getRessourceById(id: number): Observable<Ressource> {
    return this.http.get<Ressource>(`${this.apiUrl}/resources/${id}`);
  }

  createRessource(ressource: Ressource): Observable<Ressource> {
    return this.http.post<Ressource>(`${this.apiUrl}/resources`, ressource);
  }

  updateRessource(id: number, ressource: Ressource): Observable<Ressource> {
    return this.http.put<Ressource>(`${this.apiUrl}/resources/${id}`, ressource);
  }

  deleteRessource(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/resources/${id}`);
  }
}
