import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

// carga-masiva.service.ts
import { environment } from '../../../environments/environment';
import {
  CargaMasiva,
  ErrorCarga
} from '../models/carga-masiva.model';

@Injectable({
  providedIn: 'root'
})
export class CargaMasivaService {

  private readonly apiUrl =
    `${environment.apiUrl}/cargas`;

  constructor(private readonly http: HttpClient) {}

  cargarArchivo(
    archivo: File,
    usuario: string
  ): Observable<CargaMasiva> {
    const formData = new FormData();

    formData.append('archivo', archivo);
    formData.append('usuario', usuario);

    return this.http.post<CargaMasiva>(
      this.apiUrl,
      formData
    );
  }

  obtenerCarga(id: number): Observable<CargaMasiva> {
    return this.http.get<CargaMasiva>(
      `${this.apiUrl}/${id}`
    );
  }

  obtenerErrores(id: number): Observable<ErrorCarga[]> {
    return this.http.get<ErrorCarga[]>(
      `${this.apiUrl}/${id}/errores`
    );
  }
}