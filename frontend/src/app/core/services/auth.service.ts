import { Injectable, computed, signal } from '@angular/core';
import { Router } from '@angular/router';
import {
  RolUsuario,
  UsuarioSesion
} from '../models/usuario.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly STORAGE_KEY = 'devoluciones_usuario';

  private readonly usuarioSignal =
    signal<UsuarioSesion | null>(this.obtenerSesionGuardada());

  readonly usuario = this.usuarioSignal.asReadonly();

  readonly autenticado = computed(
    () => this.usuarioSignal() !== null
  );

  readonly rol = computed(
    () => this.usuarioSignal()?.rol ?? null
  );

  constructor(private readonly router: Router) {}

  login(username: string, password: string): boolean {
    const usuario = this.validarCredenciales(username, password);

    if (!usuario) {
      return false;
    }

    localStorage.setItem(
      this.STORAGE_KEY,
      JSON.stringify(usuario)
    );

    this.usuarioSignal.set(usuario);

    return true;
  }

  logout(): void {
    localStorage.removeItem(this.STORAGE_KEY);
    this.usuarioSignal.set(null);
    void this.router.navigate(['/login']);
  }

  obtenerUsuarioActual(): UsuarioSesion | null {
    return this.usuarioSignal();
  }

  obtenerToken(): string | null {
    return this.usuarioSignal()?.token ?? null;
  }

  esSupervisor(): boolean {
    return this.usuarioSignal()?.rol === 'SUPERVISOR';
  }

  private validarCredenciales(
    username: string,
    password: string
  ): UsuarioSesion | null {

    const usuarios: Record<
      string,
      { password: string; rol: RolUsuario }
    > = {
      analista1: {
        password: 'analista123',
        rol: 'ANALISTA'
      },
      supervisor1: {
        password: 'supervisor123',
        rol: 'SUPERVISOR'
      }
    };

    const usuarioEncontrado = usuarios[username];

    if (
      !usuarioEncontrado ||
      usuarioEncontrado.password !== password
    ) {
      return null;
    }

    return {
      username,
      rol: usuarioEncontrado.rol,
      token: `token-simulado-${username}`
    };
  }

  private obtenerSesionGuardada(): UsuarioSesion | null {
    const contenido = localStorage.getItem(this.STORAGE_KEY);

    if (!contenido) {
      return null;
    }

    try {
      return JSON.parse(contenido) as UsuarioSesion;
    } catch {
      localStorage.removeItem(this.STORAGE_KEY);
      return null;
    }
  }
}