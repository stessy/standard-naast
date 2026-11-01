import { Component, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { DesktopService } from './core/services/desktop.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  template: `
    @if (desktop.stopped()) {
      <main class="flex flex-column align-items-center justify-content-center min-h-screen p-4 text-center"
            role="status" aria-live="polite">
        <i class="pi pi-power-off text-4xl mb-3" aria-hidden="true"></i>
        <h1>Application arrêtée</h1>
        <p>Vous pouvez fermer cet onglet.</p>
        <p>Pour revenir, relancez Standard Naast depuis votre ordinateur.</p>
      </main>
    } @else {
      <router-outlet></router-outlet>
    }
  `
})
export class AppComponent {
  readonly desktop = inject(DesktopService);
  title = 'standardnaast-ng';
}
