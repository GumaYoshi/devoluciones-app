export type RolUsuario = 'ANALISTA' | 'SUPERVISOR';

export interface UsuarioSesion {
  username: string;
  rol: RolUsuario;
  token: string;
}