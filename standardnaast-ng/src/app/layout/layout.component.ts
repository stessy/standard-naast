import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { HeaderComponent } from './header/header.component';
import { SidebarComponent } from './sidebar/sidebar.component';
import { ToastModule } from 'primeng/toast';
import { ConfirmDialogModule } from 'primeng/confirmdialog';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    HeaderComponent,
    SidebarComponent,
    ToastModule,
    ConfirmDialogModule
  ],
  template: `
    <div class="layout-wrapper flex flex-column h-screen overflow-hidden">
      <!-- Global Header -->
      <app-header></app-header>

      <div class="layout-main flex flex-1 overflow-hidden">
        <!-- Sidebar Navigation -->
        <app-sidebar [class.sidebar-hidden]="!sidebarVisible" class="sidebar-container transition-all"></app-sidebar>

        <!-- Main Content Area -->
        <main class="layout-content flex-1 overflow-y-auto p-4 bg-slate-50">
          <router-outlet></router-outlet>
        </main>
      </div>

      <!-- Global PrimeNG Toast & ConfirmDialog -->
      <p-toast position="top-right"></p-toast>
      <p-confirmDialog [style]="{ width: '450px' }"></p-confirmDialog>
    </div>
  `,
  styles: [`
    .layout-wrapper {
      background-color: #f8fafc;
    }
    .sidebar-container {
      transition: transform 0.2s ease-in-out;
    }
    @media (max-width: 991px) {
      .sidebar-hidden {
        display: none !important;
      }
    }
  `]
})
export class LayoutComponent implements OnInit, OnDestroy {
  sidebarVisible = true;
  private toggleListener = () => {
    this.sidebarVisible = !this.sidebarVisible;
  };

  ngOnInit(): void {
    window.addEventListener('toggle-sidebar', this.toggleListener);
  }

  ngOnDestroy(): void {
    window.removeEventListener('toggle-sidebar', this.toggleListener);
  }
}
