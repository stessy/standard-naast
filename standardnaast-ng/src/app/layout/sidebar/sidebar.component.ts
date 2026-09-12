import { Component, Input, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { AuthService } from '../../core/services/auth.service';

interface NavItem {
  label: string;
  icon: string;
  route: string;
  badge?: string;
}

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterModule, ButtonModule],
  template: `
    <aside class="app-sidebar bg-white border-right-1 border-200 h-full flex flex-column">
      <!-- Nav menu items -->
      <nav class="flex-1 py-3 overflow-y-auto">
        <div class="px-3 mb-2 text-xs font-semibold text-500 uppercase tracking-wider">Navigation Principale</div>
        
        <ul class="list-none p-0 m-0">
          @for (item of mainNavItems; track item.route) {
            <li class="mb-1">
              <a
                [routerLink]="item.route"
                routerLinkActive="active-nav-item"
                [routerLinkActiveOptions]="{ exact: item.route === '/' }"
                class="flex align-items-center gap-3 px-3 py-2 border-round-lg text-700 font-medium no-underline hover:bg-red-50 hover:text-red-700 transition-colors transition-duration-150">
                <i [class]="item.icon + ' text-lg'"></i>
                <span>{{ item.label }}</span>
              </a>
            </li>
          }
        </ul>

        <div class="px-3 mt-4 mb-2 text-xs font-semibold text-500 uppercase tracking-wider">Gestion & Finances</div>
        
        <ul class="list-none p-0 m-0">
          @for (item of financeNavItems; track item.route) {
            <li class="mb-1">
              <a
                [routerLink]="item.route"
                routerLinkActive="active-nav-item"
                class="flex align-items-center gap-3 px-3 py-2 border-round-lg text-700 font-medium no-underline hover:bg-red-50 hover:text-red-700 transition-colors transition-duration-150">
                <i [class]="item.icon + ' text-lg'"></i>
                <span>{{ item.label }}</span>
              </a>
            </li>
          }
        </ul>
      </nav>

      <!-- Footer Info -->
      <div class="p-3 border-top-1 border-200 text-xs text-500 text-center">
        <span>Standard de Naast v1.0.0</span>
      </div>
    </aside>
  `,
  styles: [`
    .app-sidebar {
      width: 240px;
    }
    .active-nav-item {
      background-color: #fee2e2 !important;
      color: #b91c1c !important;
      font-weight: 700 !important;
    }
  `]
})
export class SidebarComponent {
  authService = inject(AuthService);

  mainNavItems: NavItem[] = [
    { label: 'Tableau de bord', icon: 'pi pi-home', route: '/' },
    { label: 'Membres', icon: 'pi pi-users', route: '/members' },
    { label: 'Saisons', icon: 'pi pi-calendar', route: '/seasons' },
    { label: 'Équipes', icon: 'pi pi-shield', route: '/teams' },
    { label: 'Matchs', icon: 'pi pi-calendar-times', route: '/matches' },
    { label: 'Abonnements', icon: 'pi pi-ticket', route: '/abonnements' }
  ];

  financeNavItems: NavItem[] = [
    { label: 'Cotisations', icon: 'pi pi-credit-card', route: '/cotisations' }
  ];
}
