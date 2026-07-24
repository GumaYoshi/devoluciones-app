import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';

import {
  CargaMasiva,
  ErrorCarga
} from '../../../core/models/carga-masiva.model';
import { AuthService } from '../../../core/services/auth.service';
import { CargaMasivaService } from '../../../core/services/carga-masiva.service';

@Component({
  selector: 'app-carga-masiva',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './carga-masiva.component.html',
  styleUrl: './carga-masiva.component.css'
})
export class CargaMasivaComponent {

  archivo: File | null = null;
  carga: CargaMasiva | null = null;
  errores: ErrorCarga[] = [];

  cargando = false;
  mensajeError = '';

  constructor(
    private readonly cargaService: CargaMasivaService,
    private readonly authService: AuthService
  ) {}

  seleccionarArchivo(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.archivo = input.files?.[0] ?? null;
    this.carga = null;
    this.errores = [];
    this.mensajeError = '';
  }

  subirArchivo(): void {
    if (!this.archivo) {
      this.mensajeError = 'Debe seleccionar un archivo CSV.';
      return;
    }

    const usuario =
      this.authService.obtenerUsuarioActual()?.username
      ?? 'analista1';

    this.cargando = true;
    this.mensajeError = '';

    this.cargaService.cargarArchivo(
      this.archivo,
      usuario
    ).subscribe({
      next: carga => {
        this.carga = carga;
        this.cargando = false;

        if (carga.registrosConError > 0) {
          this.cargarErrores(carga.id);
        }
      },
      error: error => {
        this.mensajeError = this.obtenerMensajeError(error);
        this.cargando = false;
      }
    });
  }

  private cargarErrores(id: number): void {
    this.cargaService.obtenerErrores(id).subscribe({
      next: errores => {
        this.errores = errores;
      },
      error: error => {
        this.mensajeError = this.obtenerMensajeError(error);
      }
    });
  }

  private obtenerMensajeError(error: any): string {
    return error?.error?.detalle
      ?? error?.error?.message
      ?? 'No fue posible procesar el archivo.';
  }
}