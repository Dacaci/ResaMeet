export enum ReservationStatus {
  EN_ATTENTE = 'EN_ATTENTE',
  CONFIRMEE = 'CONFIRMEE',
  REFUSEE = 'REFUSEE',
  ANNULEE = 'ANNULEE'
}

export interface Reservation {
  id?: number;
  resourceId: number;
  userId?: number;
  username?: string;
  ressourceNom?: string;
  dateDebut: string;
  dateFin: string;
  status?: ReservationStatus;
  dateAnnulation?: string;
}
