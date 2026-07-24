import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

// solicitud.service.ts
import { environment } from '../../../environments/environment';

import {
  ActualizarSolicitudRequest,
  CambiarEstadoRequest,
  CrearSolicitudRequest,
  EventoSolicitud,
  Solicitud
} from '../models/solicitud.model';

@Injectable({
  providedIn: 'root'
})
export class SolicitudService {

  private readonly apiUrl =
    `${environment.apiUrl}/solicitudes`;

  constructor(private readonly http: HttpClient) {}

  obtenerTodas(): Observable<Solicitud[]> {
    return this.http.get<Solicitud[]>(this.apiUrl);
  }

  obtenerPorId(id: number): Observable<Solicitud> {
    return this.http.get<Solicitud>(
      `${this.apiUrl}/${id}`
    );
  }

  crear(
    request: CrearSolicitudRequest
  ): Observable<Solicitud> {
    return this.http.post<Solicitud>(
      this.apiUrl,
      request
    );
  }

  actualizar(
    id: number,
    request: ActualizarSolicitudRequest
  ): Observable<Solicitud> {
    return this.http.put<Solicitud>(
      `${this.apiUrl}/${id}`,
      request
    );
  }

  cambiarEstado(
    id: number,
    request: CambiarEstadoRequest
  ): Observable<Solicitud> {
    return this.http.patch<Solicitud>(
      `${this.apiUrl}/${id}/estado`,
      request
    );
  }

  obtenerHistorial(
    id: number
  ): Observable<EventoSolicitud[]> {
    return this.http.get<EventoSolicitud[]>(
      `${this.apiUrl}/${id}/historial`
    );
  }
}