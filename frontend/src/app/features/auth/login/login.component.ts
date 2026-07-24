import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';

import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {

  readonly errorMensaje = signal<string | null>(null);

  readonly formulario = this.formBuilder.nonNullable.group({
    username: ['', Validators.required],
    password: ['', Validators.required]
  });

  constructor(
    private readonly formBuilder: FormBuilder,
    private readonly authService: AuthService,
    private readonly router: Router
  ) {}

  ingresar(): void {
    this.errorMensaje.set(null);

    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }

    const { username, password } =
      this.formulario.getRawValue();

    const loginCorrecto =
      this.authService.login(username, password);

    if (!loginCorrecto) {
      this.errorMensaje.set(
        'Usuario o contraseña incorrectos'
      );
      return;
    }

    void this.router.navigate(['/solicitudes']);
  }
}