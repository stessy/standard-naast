import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { ToolbarModule } from 'primeng/toolbar';
import { ButtonModule } from 'primeng/button';
import { SelectModule } from 'primeng/select';
import { DropdownModule } from 'primeng/dropdown';
import { AvatarModule } from 'primeng/avatar';
import { MenuModule } from 'primeng/menu';
import { MenuItem } from 'primeng/api';
import { AuthService } from '../../core/services/auth.service';
import { SeasonService } from '../../core/services/season.service';
import { Season } from '../../core/models/season.model';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
    ToolbarModule,
    ButtonModule,
    DropdownModule,
    AvatarModule,
    MenuModule
  ],
  template: `
    <header class="app-header shadow-1">
      <div class="flex align-items-center justify-content-between px-4 py-2 bg-red-700 text-white">
        <!-- Logo & Title -->
        <div class="flex align-items-center gap-3">
          <button pButton icon="pi pi-bars" [text]="true" class="text-white p-button-rounded lg:hidden" (click)="toggleSidebar()"></button>
          <a routerLink="/" class="flex align-items-center gap-2 no-underline text-white">
            <i class="pi pi-shield text-2xl text-yellow-400"></i>
            <span class="font-bold text-xl tracking-wide uppercase">Standard de Naast</span>
          </a>
        </div>

        <!-- Global Season Selector & User Profile -->
        <div class="flex align-items-center gap-3">
          <!-- Season Dropdown -->
          <div class="flex align-items-center gap-2 bg-red-800 px-3 py-1 border-round-lg">
            <i class="pi pi-calendar text-yellow-400"></i>
            <span class="text-sm font-semibold hidden md:inline">Saison :</span>
            <p-dropdown
              [options]="seasons"
              optionLabel="id"
              [(ngModel)]="selectedSeason"
              (onChange)="onSeasonChange($event.value)"
              placeholder="Choisir une saison"
              styleClass="p-dropdown-sm bg-transparent border-none text-white font-bold"
              [panelStyleClass]="'season-dropdown-panel'">
            </p-dropdown>
          </div>

          <!-- User Menu -->
          @if (authService.currentUser(); as user) {
            <div class="flex align-items-center gap-2 pl-2 border-left-1 border-red-600">
              <span class="font-medium text-sm hidden sm:inline">{{ user.firstname }} {{ user.lastname }}</span>
              <button
                pButton
                icon="pi pi-user"
                class="p-button-rounded p-button-text text-white bg-red-800"
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
    }
    :host ::ng-deep .p-dropdown .p-dropdown-label {
      color: white !important;
      font-weight: 600;
    }
    :host ::ng-deep .p-dropdown .p-dropdown-trigger {
      color: white !important;
    }
  `]
})
export class HeaderComponent implements OnInit {
  authService = inject(AuthService);
  seasonService = inject(SeasonService);
  router = inject(Router);

  seasons: Season[] = [];
  selectedSeason: Season | null = null;
  userMenuItems: MenuItem[] = [];

  ngOnInit(): void {
    this.loadSeasons();
    this.initUserMenu();
  }

  loadSeasons(): void {
    this.seasonService.getAllSeasons().subscribe({
      next: (data) => {
        this.seasons = data;
        const current = this.seasonService.selectedSeason();
        if (current) {
          this.selectedSeason = this.seasons.find(s => s.id === current.id) || current;
        } else if (this.seasons.length > 0) {
          this.seasonService.getCurrentSeason().subscribe({
            next: (s) => {
              this.selectedSeason = this.seasons.find(item => item.id === s.id) || s;
            },
            error: () => {
              this.selectedSeason = this.seasons[0];
              this.seasonService.setSelectedSeason(this.seasons[0]);
            }
          });
        }
      }
    });
  }

  onSeasonChange(season: Season): void {
    if (season) {
      this.seasonService.setSelectedSeason(season);
    }
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
