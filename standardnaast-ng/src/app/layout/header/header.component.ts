import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { AvatarModule } from 'primeng/avatar';
import { MenuModule } from 'primeng/menu';
import { MenuItem } from 'primeng/api';
import { AuthService } from '../../core/services/auth.service';
import { SeasonService } from '../../core/services/season.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [
    CommonModule,
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
          <button pButton icon="pi pi-bars" [text]="true" class="text-red-700 p-button-rounded lg:hidden" (click)="toggleSidebar()"></button>
          <a routerLink="/" class="flex align-items-center gap-2 no-underline text-red-700">
            <i class="pi pi-shield text-2xl text-red-600"></i>
            <span class="font-bold text-xl tracking-wide uppercase hidden sm:inline">Standard de Naast</span>
          </a>
        </div>

        <!-- Centered Banner Image -->
        <div class="flex-1 flex justify-content-center align-items-center px-2">
          <img src="assets/images/banniere_club.png" alt="Standard de Naast" class="header-banner" />
        </div>

        <!-- User Profile -->
        <div class="flex align-items-center gap-3">
          @if (authService.currentUser(); as user) {
            <div class="flex align-items-center gap-2 pl-2 border-left-1 border-red-300">
              <span class="font-medium text-sm text-red-700 hidden sm:inline">{{ user.firstname }} {{ user.lastname }}</span>
              <button
                pButton
                icon="pi pi-user"
                class="p-button-rounded p-button-text text-red-700 hover:bg-red-200"
                (click)="userMenu.toggle($event)">
              </button>
              <p-menu #userMenu [model]="userMenuItems" [popup]="true"></p-menu>
            </div>
          }
        </div>
      </div>
    </header>
  `,
  styles: [`
    .app-header {
      position: sticky;
      top: 0;
      z-index: 1000;
      background-color: #fee2e2;
      border-bottom: 1px solid #fecaca;
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

  userMenuItems: MenuItem[] = [];

  ngOnInit(): void {
    if (!this.seasonService.selectedSeason()) {
      this.seasonService.getCurrentSeason().subscribe({
        error: () => {
          this.seasonService.getAllSeasons().subscribe({
            next: (seasons) => {
              if (seasons.length > 0) {
                this.seasonService.setSelectedSeason(seasons[0]);
              }
            }
          });
        }
      });
    }
    this.initUserMenu();
  }

  toggleSidebar(): void {
    // Custom event to notify layout
    window.dispatchEvent(new CustomEvent('toggle-sidebar'));
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
