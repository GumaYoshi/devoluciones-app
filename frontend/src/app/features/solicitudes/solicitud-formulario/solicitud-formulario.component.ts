import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  ReactiveFormsModule,
  ValidationErrors,
  ValidatorFn,
  Validators
} from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { AuthService } from '../../../core/services/auth.service';
import { SolicitudService } from '../../../core/services/solicitud.service';

const rutChilenoValidator: ValidatorFn =
  (control: AbstractControl): ValidationErrors | null => {
    const valor = String(control.value ?? '')
      .replace(/\./g, '')
      .replace(/-/g, '')
      .trim()
      .toUpperCase();

    if (!valor) {
      return null;
    }

    if (!/^\d{7,8}[0-9K]$/.test(valor)) {
      return { rutInvalido: true };
    }

    const cuerpo = valor.slice(0, -1);
    const dvIngresado = valor.slice(-1);

    let suma = 0;
    let multiplicador = 2;

    for (let i = cuerpo.length - 1; i >= 0; i--) {
      suma += Number(cuerpo[i]) * multiplicador;
      multiplicador =
        multiplicador === 7 ? 2 : multiplicador + 1;
    }

    const resultado = 11 - (suma % 11);

    const dvCalculado =
      resultado === 11
        ? '0'
        : resultado === 10
          ? 'K'
          : String(resultado);

    return dvIngresado === dvCalculado
      ? null
      : { rutInvalido: true };
  };

@Component({
  selector: 'app-solicitud-formulario',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink
  ],
  templateUrl: './solicitud-formulario.component.html',
  styleUrl: './solicitud-formulario.component.css'
})
export class SolicitudFormularioComponent implements OnInit {

  solicitudId: number | null = null;
  editando = false;
  guardando = false;
  mensajeError = '';

  readonly formulario = this.formBuilder.nonNullable.group({
    folio: [
      '',
      [
        Validators.required,
        Validators.pattern(/^DEV-\d{4}-\d{6}$/)
      ]
    ],
    rutCliente: [
      '',
      [
        Validators.required,
        rutChilenoValidator
      ]
    ],
    nombreCliente: ['', Validators.required],
    monto: [
      0,
      [
        Validators.required,
        Validators.min(0.01),
        Validators.max(10000000)
      ]
    ],
    moneda: ['CLP'],
    bancoDestino: ['', Validators.required],
    cuentaDestino: ['', Validators.required],
    referenciaBanco: ['', Validators.required]
  });

  constructor(
    private readonly formBuilder: FormBuilder,
    private readonly solicitudService: SolicitudService,
    private readonly authService: AuthService,
    private readonly route: ActivatedRoute,
    private readonly router: Router
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');

    if (id) {
      this.solicitudId = Number(id);
      this.editando = true;
      this.cargarSolicitud(this.solicitudId);
    }
  }

  erroresCampos: Record<string, string> = {};

  guardar(): void {
    this.mensajeError = '';
    this.erroresCampos = {};

  if (this.formulario.invalid) {
    this.formulario.markAllAsTouched();
    this.guardando = false;
    this.mensajeError =
      'Revise los campos marcados antes de guardar.';
    return;
  }

    this.guardando = true;
    const datos = this.formulario.getRawValue();

    if (this.editando && this.solicitudId) {
      this.solicitudService.actualizar(
        this.solicitudId,
        {
          rutCliente: datos.rutCliente,
          nombreCliente: datos.nombreCliente,
          monto: datos.monto,
          moneda: 'CLP',
          bancoDestino: datos.bancoDestino,
          cuentaDestino: datos.cuentaDestino
        }
      ).subscribe({
        next: solicitud => {
          void this.router.navigate([
            '/solicitudes',
            solicitud.id
          ]);
        },
        error: error => {
          this.procesarErrorBackend(error);
          this.guardando = false;
        }
      });

      return;
    }

    const usuario =
      this.authService.obtenerUsuarioActual()?.username
      ?? 'analista1';

    this.solicitudService.crear({
      folio: datos.folio,
      rutCliente: datos.rutCliente,
      nombreCliente: datos.nombreCliente,
      monto: datos.monto,
      moneda: 'CLP',
      bancoDestino: datos.bancoDestino,
      cuentaDestino: datos.cuentaDestino,
      referenciaBanco: datos.referenciaBanco,
      origen: 'MANUAL',
      creadaPor: usuario
    }).subscribe({
      next: solicitud => {
        void this.router.navigate([
          '/solicitudes',
          solicitud.id
        ]);
      },
      error: error => {
        this.procesarErrorBackend(error);
        this.guardando = false;
      }
    });
  }

  private cargarSolicitud(id: number): void {
    this.solicitudService.obtenerPorId(id).subscribe({
      next: solicitud => {
        this.formulario.patchValue({
          folio: solicitud.folio,
          rutCliente: solicitud.rutCliente,
          nombreCliente: solicitud.nombreCliente,
          monto: solicitud.monto,
          moneda: solicitud.moneda,
          bancoDestino: solicitud.bancoDestino,
          cuentaDestino: solicitud.cuentaDestino,
          referenciaBanco: solicitud.referenciaBanco
        });

        this.formulario.controls.folio.disable();
        this.formulario.controls.referenciaBanco.disable();
      },
      error: error => {
        this.procesarErrorBackend(error);
      }
    });
  }

  private obtenerMensajeError(error: any): string {
    return error?.error?.detalle
      ?? error?.error?.mensaje
      ?? error?.error?.message
      ?? error?.message
      ?? 'No fue posible guardar la solicitud.';
  }

  private procesarErrorBackend(error: any): void {
    this.erroresCampos =
      error?.error?.validationErrors
      ?? {};

    const mensajes =
      Object.values(this.erroresCampos);

    if (mensajes.length > 0) {
      this.mensajeError = mensajes.join('. ');
      return;
    }

    if (error.status === 409) {
      this.mensajeError =
        this.obtenerMensajeError(error)
        || 'El folio o la referencia bancaria ya existen.';
      return;
    }

    this.mensajeError =
      this.obtenerMensajeError(error);
  }
}