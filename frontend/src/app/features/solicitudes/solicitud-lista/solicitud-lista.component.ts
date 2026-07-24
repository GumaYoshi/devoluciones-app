import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import {
  EstadoSolicitud,
  Solicitud
} from '../../../core/models/solicitud.model';
import { SolicitudService } from '../../../core/services/solicitud.service';

@Component({
  selector: 'app-solicitud-lista',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterLink
  ],
  templateUrl: './solicitud-lista.component.html',
  styleUrl: './solicitud-lista.component.css'
})
export class SolicitudListaComponent implements OnInit {

  solicitudes: Solicitud[] = [];
  solicitudesFiltradas: Solicitud[] = [];

  cargando = false;
  mensajeError = '';

  filtroRut = '';
  filtroEstado = '';

  constructor(
    private readonly solicitudService: SolicitudService
  ) {}

  ngOnInit(): void {
    this.cargarSolicitudes();
  }

  cargarSolicitudes(): void {
    this.cargando = true;
    this.mensajeError = '';

    this.solicitudService.obtenerTodas().subscribe({
      next: solicitudes => {
        this.solicitudes = solicitudes;
        this.aplicarFiltros();
        this.cargando = false;
      },
      error: error => {
        this.mensajeError = this.obtenerMensajeError(error);
        this.cargando = false;
      }
    });
  }

  aplicarFiltros(): void {
    const rut = this.filtroRut.trim().toLowerCase();

    this.solicitudesFiltradas = this.solicitudes.filter(
      solicitud => {
        const coincideRut =
          !rut ||
          solicitud.rutCliente.toLowerCase().includes(rut);

        const coincideEstado =
          !this.filtroEstado ||
          solicitud.estado === this.filtroEstado;

        return coincideRut && coincideEstado;
      }
    );
  }

  limpiarFiltros(): void {
    this.filtroRut = '';
    this.filtroEstado = '';
    this.aplicarFiltros();
  }

  claseEstado(estado: EstadoSolicitud): string {
    return `estado estado-${estado.toLowerCase()}`;
  }

  formatearMonto(monto: number): string {
    return new Intl.NumberFormat('es-CL', {
      style: 'currency',
      currency: 'CLP',
      maximumFractionDigits: 0
    }).format(monto);
  }

  private obtenerMensajeError(error: any): string {
    return error?.error?.detalle
      ?? error?.error?.message
      ?? 'No fue posible obtener las solicitudes.';
  }
}