import { Component, OnInit, inject, ChangeDetectionStrategy } from '@angular/core';

import { Router, RouterModule } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { AvatarModule } from 'primeng/avatar';
import { MenuModule } from 'primeng/menu';
import { ConfirmationService, MenuItem, MessageService } from 'primeng/api';
import { DesktopService } from '../../core/services/desktop.service';
import { AuthService } from '../../core/services/auth.service';
import { SeasonService } from '../../core/services/season.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [
    RouterModule,
    ButtonModule,
    AvatarModule,
    MenuModule
],
  template: `
    <header class="app-header shadow-1">
      <div class="header-container flex align-items-center justify-content-between px-4 py-2">
        <!-- Logo & Title -->
        <div class="flex align-items-center gap-3">
          <button pButton icon="pi pi-bars" [text]="true" class="text-white p-button-rounded lg:hidden" (click)="toggleSidebar()"></button>
          <a routerLink="/" class="flex align-items-center gap-2 no-underline text-white">
            <i class="pi pi-shield text-2xl text-yellow-400"></i>
            <span class="font-bold text-xl tracking-wide uppercase hidden sm:inline">Standard de Naast</span>
          </a>
        </div>

        <!-- Centered Banner Image -->
        <div class="flex-1 flex justify-content-center align-items-center px-2">
          <img src="assets/images/banniere_club.png" alt="Standard de Naast" class="header-banner" />
        </div>

        <!-- User Profile -->
        <div class="flex align-items-center gap-3">
          @if (desktop.available()) {
            @if (authService.isAdmin()) {
              <button pButton icon="pi pi-cog" label="Paramètres" routerLink="/settings"
                      class="p-button-text text-white" [disabled]="desktop.stopping()"></button>
            }
            <button pButton icon="pi pi-power-off" label="Quitter l'application"
                    class="p-button-text text-white" [disabled]="desktop.stopping()"
                    [loading]="desktop.stopping()" (click)="quitApplication()"></button>
          }
          @if (authService.currentUser(); as user) {
            <div class="flex align-items-center gap-2 pl-2 border-left-1 border-red-600">
              <span class="font-medium text-sm text-white hidden sm:inline">{{ user.firstname }} {{ user.lastname }}</span>
              <button
                pButton
                icon="pi pi-user"
                class="p-button-rounded p-button-text text-white hover:bg-red-800"
                (click)="userMenu.toggle($event)">
              </button>
              <p-menu #userMenu [model]="userMenuItems" [popup]="true"></p-menu>
            </div>
          }
        </div>
      </div>
    </header>
  `,
  changeDetection: ChangeDetectionStrategy.Eager,
  styles: [`
    .app-header {
      position: sticky;
      top: 0;
      z-index: 1000;
      background-color: #b91c1c;
      border-bottom: 1px solid #991b1b;
    }
    .header-container {
      min-height: 56px;
    }
    .header-banner {
      max-height: 48px;
      max-width: 100%;
      object-fit: contain;
    }
  `]
})
export class HeaderComponent implements OnInit {
  authService = inject(AuthService);
  seasonService = inject(SeasonService);
  router = inject(Router);
  readonly desktop = inject(DesktopService);
  private readonly confirmation = inject(ConfirmationService);
  private readonly messages = inject(MessageService);

  userMenuItems: MenuItem[] = [];

  ngOnInit(): void {
    this.desktop.loadRuntime().subscribe({
      error: () => this.messages.add({
        severity: 'error',
        summary: 'Mode application indisponible',
        detail: "Impossible de vérifier si l'application peut être arrêtée depuis cette interface."
      })
    });
    this.seasonService.getCurrentSeason().subscribe({
      error: () => {
        if (!this.seasonService.selectedSeason()) {
          this.seasonService.getAllSeasons().subscribe({
            next: (seasons) => {
              if (seasons.length > 0) {
                this.seasonService.setSelectedSeason(seasons[0]);
              }
            }
          });
        }
      }
    });
    this.initUserMenu();
  }

  toggleSidebar(): void {
    // Custom event to notify layout
    window.dispatchEvent(new CustomEvent('toggle-sidebar'));
  }

  quitApplication(): void {
    if (this.desktop.stopping() || !this.desktop.available()) return;
    this.confirmation.confirm({
      header: "Quitter l'application",
      message: "L'application sera arrêtée pour tous les onglets. Les modifications non enregistrées seront perdues. Continuer ?",
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Quitter',
      rejectLabel: 'Annuler',
      accept: () => {
        if (this.desktop.stopping()) return;
        this.desktop.quit().subscribe({
          error: (error) => {
            this.messages.add({
              severity: 'error',
              summary: 'Arrêt impossible',
              detail: error.error?.detail ?? "L'arrêt n'a pas pu être confirmé. Vérifiez le dossier de sauvegarde et réessayez."
            });
          }
        });
      }
    });
  }

  initUserMenu(): void {
    this.userMenuItems = [
      {
        label: 'Mon Profil',
        icon: 'pi pi-user',
        command: () => this.router.navigate(['/profile'])
      },
      {
        separator: true
      },
      {
        label: 'Déconnexion',
        icon: 'pi pi-sign-out',
        command: () => this.authService.logout()
      }
    ];
  }
}
