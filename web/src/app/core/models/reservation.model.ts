export enum ReservationStatus {
  PENDING = 'PENDING',
  CONFIRMED = 'CONFIRMED',
  CANCELED = 'CANCELED',
  REFUSED = 'REFUSED'
}

export interface Reservation {
  id?: number;
  resourceId: number;
  userId?: number;
  startDateTime: string;
  endDateTime: string;
  nbParticipants: number;
  status?: ReservationStatus;
}
