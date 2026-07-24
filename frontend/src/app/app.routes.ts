import { Routes } from '@angular/router';

import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () =>
      import('./features/auth/login/login.component')
        .then(component => component.LoginComponent)
  },
  {
    path: 'solicitudes',
    canActivate: [authGuard],
    loadComponent: () =>
      import(
        './features/solicitudes/solicitud-lista/solicitud-lista.component'
      ).then(component => component.SolicitudListaComponent)
  },
  {
    path: 'solicitudes/nueva',
    canActivate: [authGuard],
    loadComponent: () =>
      import(
        './features/solicitudes/solicitud-formulario/solicitud-formulario.component'
      ).then(component => component.SolicitudFormularioComponent)
  },
  {
    path: 'solicitudes/:id/editar',
    canActivate: [authGuard],
    loadComponent: () =>
      import(
        './features/solicitudes/solicitud-formulario/solicitud-formulario.component'
      ).then(component => component.SolicitudFormularioComponent)
  },
  {
    path: 'solicitudes/:id',
    canActivate: [authGuard],
    loadComponent: () =>
      import(
        './features/solicitudes/solicitud-detalle/solicitud-detalle.component'
      ).then(component => component.SolicitudDetalleComponent)
  },
  {
    path: 'cargas',
    canActivate: [authGuard],
    loadComponent: () =>
      import(
        './features/cargas/carga-masiva/carga-masiva.component'
      ).then(component => component.CargaMasivaComponent)
  },
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'solicitudes'
  },
  {
    path: '**',
    redirectTo: 'solicitudes'
  }
];