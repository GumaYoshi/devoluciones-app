import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

import {
  NavbarComponent
} from './layout/navbar/navbar.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet,
    NavbarComponent
  ],
  template: `
    <app-navbar />
    <main class="contenedor-principal">
      <router-outlet />
    </main>
  `,
  styles: `
    .contenedor-principal {
      width: min(1200px, calc(100% - 32px));
      margin: 0 auto;
      padding: 24px 0 48px;
    }
  `
})
export class AppComponent {}