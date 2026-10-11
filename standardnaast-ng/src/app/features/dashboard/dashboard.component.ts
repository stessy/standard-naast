import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { CardModule } from 'primeng/card';
import { ButtonModule } from 'primeng/button';
import { TagModule } from 'primeng/tag';
import { SeasonService } from '../../core/services/season.service';
import { MemberService } from '../../core/services/member.service';
import { CotisationService } from '../../core/services/cotisation.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    CardModule,
    ButtonModule,
    TagModule
  ],
  template: `
    <div class="dashboard-page flex flex-column gap-4">
      <!-- Welcome Header -->
      <div class="flex flex-column md:flex-row md:align-items-center md:justify-content-between gap-3 bg-white p-4 border-round-xl border-1 border-200 shadow-1">
        <div>
          <h1 class="text-2xl font-bold text-900 m-0">Tableau de bord</h1>
          <p class="text-500 m-0 mt-1">Aperçu général des activités et indicateurs du club</p>
        </div>
        @if (seasonService.selectedSeason(); as season) {
          <div class="flex align-items-center gap-2">
            <span class="text-sm font-semibold text-600">Saison active :</span>
            <span class="bg-red-100 text-red-800 font-bold px-3 py-1 border-round-lg text-base">{{ season.id }}</span>
          </div>
        }
      </div>

      <!-- KPI Metrics Cards -->
      <div class="grid">
        <!-- Members Count -->
        <div class="col-12 md:col-6">
          <div class="surface-card p-4 border-round-xl border-1 border-200 shadow-1 flex flex-column justify-content-between h-full">
            <div class="flex justify-content-between mb-3">
              <div>
                <span class="block text-500 font-medium mb-2">Membres Actifs</span>
                <div class="text-900 font-bold text-3xl">{{ totalMembers }}</div>
              </div>
              <div class="flex align-items-center justify-content-center bg-blue-100 border-round" style="width: 2.75rem; height: 2.75rem">
                <i class="pi pi-users text-blue-600 text-xl"></i>
              </div>
            </div>
            <a routerLink="/members" class="text-sm text-blue-600 font-semibold no-underline hover:underline">Voir les membres &rarr;</a>
          </div>
        </div>

        <!-- Cotisations overview -->
        <div class="col-12 md:col-6">
          <div class="surface-card p-4 border-round-xl border-1 border-200 shadow-1 flex flex-column justify-content-between h-full">
            <div class="flex justify-content-between mb-3">
              <div>
                <span class="block text-500 font-medium mb-2">Cotisations à jour</span>
                <div class="text-900 font-bold text-3xl">{{ paidCotisations }}</div>
                <span class="text-xs text-orange-500 font-semibold">{{ unpaidCotisations }} en attente</span>
              </div>
              <div class="flex align-items-center justify-content-center bg-green-100 border-round" style="width: 2.75rem; height: 2.75rem">
                <i class="pi pi-credit-card text-green-600 text-xl"></i>
              </div>
            </div>
            <a routerLink="/cotisations" class="text-sm text-green-600 font-semibold no-underline hover:underline">Gestion des cotisations &rarr;</a>
          </div>
        </div>
      </div>

      <!-- Quick Actions Grid -->
      <div class="surface-card p-4 border-round-xl border-1 border-200 shadow-1">
        <h2 class="text-lg font-bold text-900 mb-3">Accès Rapides</h2>
        <div class="flex flex-wrap gap-3">
          <button pButton label="Nouveau Membre" icon="pi pi-user-plus" class="p-button-outlined p-button-danger" routerLink="/members"></button>
          <button pButton label="Gérer les Abonnements" icon="pi pi-ticket" class="p-button-outlined p-button-secondary" routerLink="/abonnements"></button>
        </div>
      </div>
    </div>
  `
})
export class DashboardComponent implements OnInit {
  seasonService = inject(SeasonService);
  memberService = inject(MemberService);
  cotisationService = inject(CotisationService);

  totalMembers = 0;
  paidCotisations = 0;
  unpaidCotisations = 0;

  ngOnInit(): void {
    this.loadStats();
  }

  loadStats(): void {
    // Calcul des membres actifs (numéro de membre < 1000)
    this.memberService.getActiveMemberCount().subscribe({
      next: (count) => this.totalMembers = count
    });

    // Season data
    const currentSeason = this.seasonService.selectedSeason();
    if (currentSeason) {
      this.loadCotisationOverview(currentSeason.id);
    } else {
      this.seasonService.getCurrentSeason().subscribe({
        next: (season) => {
          if (season) {
            this.loadCotisationOverview(season.id);
          }
        },
        error: () => {
          this.seasonService.getAllSeasons().subscribe({
            next: (seasons) => {
              if (seasons.length > 0) {
                this.loadCotisationOverview(seasons[0].id);
              }
            }
          });
        }
      });
    }
  }

  private loadCotisationOverview(seasonId: string): void {
    this.cotisationService.getSeasonOverview(seasonId).subscribe({
      next: (ov) => {
        this.paidCotisations = ov.totalPaid;
        this.unpaidCotisations = ov.totalUnpaid;
      }
    });
  }
}
