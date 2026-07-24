import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';

import {
  EstadoSolicitud,
  EventoSolicitud,
  Solicitud
} from '../../../core/models/solicitud.model';
import { AuthService } from '../../../core/services/auth.service';
import { SolicitudService } from '../../../core/services/solicitud.service';

@Component({
  selector: 'app-solicitud-detalle',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterLink
  ],
  templateUrl: './solicitud-detalle.component.html',
  styleUrl: './solicitud-detalle.component.css'
})
export class SolicitudDetalleComponent implements OnInit {

  solicitud: Solicitud | null = null;
  historial: EventoSolicitud[] = [];

  cargando = true;
  procesando = false;
  mensajeError = '';
  comentario = '';

  constructor(
    private readonly route: ActivatedRoute,
    readonly authService: AuthService,
    private readonly solicitudService: SolicitudService
  ) {}

  ngOnInit(): void {
    const id = Number(
      this.route.snapshot.paramMap.get('id')
    );

    this.cargarDetalle(id);
  }

  cambiarEstado(nuevoEstado: EstadoSolicitud): void {
    if (!this.solicitud) {
      return;
    }

    if (
      nuevoEstado === 'RECHAZADA' &&
      !this.comentario.trim()
    ) {
      this.mensajeError =
        'Debe ingresar el motivo del rechazo.';
      return;
    }

    const usuario =
      this.authService.obtenerUsuarioActual()?.username
      ?? 'analista1';

    this.procesando = true;
    this.mensajeError = '';

    this.solicitudService.cambiarEstado(
      this.solicitud.id,
      {
        nuevoEstado,
        usuario,
        comentario: this.comentario.trim() || null
      }
    ).subscribe({
      next: solicitud => {
        this.solicitud = solicitud;
        this.comentario = '';
        this.procesando = false;
        this.cargarHistorial(solicitud.id);
      },
      error: error => {
        this.mensajeError = this.obtenerMensajeError(error);
        this.procesando = false;
      }
    });
  }

  puedeEnviar(): boolean {
    return this.solicitud?.estado === 'BORRADOR';
  }

  puedeAnular(): boolean {
    return this.solicitud?.estado === 'BORRADOR';
  }

  puedeAprobarORechazar(): boolean {
    return (
      this.solicitud?.estado === 'EN_REVISION' &&
      this.authService.esSupervisor()
    );
  }

  puedePagar(): boolean {
    return (
      this.solicitud?.estado === 'APROBADA' &&
      this.authService.esSupervisor()
    );
  }

  puedeReabrir(): boolean {
    return this.solicitud?.estado === 'RECHAZADA';
  }

  formatearMonto(monto: number): string {
    return new Intl.NumberFormat('es-CL', {
      style: 'currency',
      currency: 'CLP',
      maximumFractionDigits: 0
    }).format(monto);
  }

  private cargarDetalle(id: number): void {
    this.solicitudService.obtenerPorId(id).subscribe({
      next: solicitud => {
        this.solicitud = solicitud;
        this.cargando = false;
        this.cargarHistorial(id);
      },
      error: error => {
        this.mensajeError = this.obtenerMensajeError(error);
        this.cargando = false;
      }
    });
  }

  private cargarHistorial(id: number): void {
    this.solicitudService.obtenerHistorial(id).subscribe({
      next: historial => {
        this.historial = historial;
      },
      error: error => {
        this.mensajeError = this.obtenerMensajeError(error);
      }
    });
  }

  private obtenerMensajeError(error: any): string {
    return error?.error?.detalle
      ?? error?.error?.message
      ?? 'Ocurrió un error al procesar la solicitud.';
  }
}