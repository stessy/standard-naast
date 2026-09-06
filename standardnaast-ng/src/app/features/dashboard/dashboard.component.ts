import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { CardModule } from 'primeng/card';
import { ButtonModule } from 'primeng/button';
import { TagModule } from 'primeng/tag';
import { SeasonService } from '../../core/services/season.service';
import { MemberService } from '../../core/services/member.service';
import { MatchService } from '../../core/services/match.service';
import { CotisationService } from '../../core/services/cotisation.service';
import { AccountingService } from '../../core/services/accounting.service';
import { Match } from '../../core/models/match.model';

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
        <div class="col-12 sm:col-6 lg:col-3">
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

        <!-- Next Match -->
        <div class="col-12 sm:col-6 lg:col-3">
          <div class="surface-card p-4 border-round-xl border-1 border-200 shadow-1 flex flex-column justify-content-between h-full">
            <div class="flex justify-content-between mb-3">
              <div>
                <span class="block text-500 font-medium mb-2">Prochain Match</span>
                <div class="text-900 font-bold text-xl">{{ nextMatch ? nextMatch.opponentName : 'Aucun match' }}</div>
                @if (nextMatch) {
                  <span class="text-xs text-500 font-medium">{{ nextMatch.dateMatch | date:'dd/MM/yyyy HH:mm' }} ({{ nextMatch.place === 'HOME' ? 'Domicile' : 'Extérieur' }})</span>
                }
              </div>
              <div class="flex align-items-center justify-content-center bg-red-100 border-round" style="width: 2.75rem; height: 2.75rem">
                <i class="pi pi-calendar-times text-red-600 text-xl"></i>
              </div>
            </div>
            <a routerLink="/matches" class="text-sm text-red-600 font-semibold no-underline hover:underline">Calendrier complet &rarr;</a>
          </div>
        </div>

        <!-- Cotisations overview -->
        <div class="col-12 sm:col-6 lg:col-3">
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

        <!-- Balance overview -->
        <div class="col-12 sm:col-6 lg:col-3">
          <div class="surface-card p-4 border-round-xl border-1 border-200 shadow-1 flex flex-column justify-content-between h-full">
            <div class="flex justify-content-between mb-3">
              <div>
                <span class="block text-500 font-medium mb-2">Solde Comptable</span>
                <div class="text-900 font-bold text-3xl" [class.text-green-600]="accountingBalance >= 0" [class.text-red-600]="accountingBalance < 0">
                  {{ accountingBalance | currency:'EUR':'symbol':'1.2-2':'fr' }}
                </div>
              </div>
              <div class="flex align-items-center justify-content-center bg-purple-100 border-round" style="width: 2.75rem; height: 2.75rem">
                <i class="pi pi-wallet text-purple-600 text-xl"></i>
              </div>
            </div>
            <a routerLink="/accounting" class="text-sm text-purple-600 font-semibold no-underline hover:underline">Journal comptable &rarr;</a>
          </div>
        </div>
      </div>

      <!-- Quick Actions Grid -->
      <div class="surface-card p-4 border-round-xl border-1 border-200 shadow-1">
        <h2 class="text-lg font-bold text-900 mb-3">Accès Rapides</h2>
        <div class="flex flex-wrap gap-3">
          <button pButton label="Nouveau Membre" icon="pi pi-user-plus" class="p-button-outlined p-button-danger" routerLink="/members"></button>
          <button pButton label="Planifier un Voyage" icon="pi pi-car" class="p-button-outlined p-button-secondary" routerLink="/travels"></button>
          <button pButton label="Gérer les Abonnements" icon="pi pi-ticket" class="p-button-outlined p-button-secondary" routerLink="/abonnements"></button>
          <button pButton label="Nouvelle Écriture Comptable" icon="pi pi-plus" class="p-button-outlined p-button-secondary" routerLink="/accounting"></button>
        </div>
      </div>
    </div>
  `
})
export class DashboardComponent implements OnInit {
  seasonService = inject(SeasonService);
  memberService = inject(MemberService);
  matchService = inject(MatchService);
  cotisationService = inject(CotisationService);
  accountingService = inject(AccountingService);

  totalMembers = 0;
  nextMatch: Match | null = null;
  paidCotisations = 0;
  unpaidCotisations = 0;
  accountingBalance = 0;

  ngOnInit(): void {
    this.loadStats();
  }

  loadStats(): void {
    // Member count
    this.memberService.getMembers(undefined, 0, 1).subscribe({
      next: (page) => this.totalMembers = page.totalElements
    });

    // Accounting summary
    this.accountingService.getSummary().subscribe({
      next: (summary) => this.accountingBalance = summary.balance
    });

    // Season data
    const currentSeason = this.seasonService.selectedSeason();
    if (currentSeason) {
      this.cotisationService.getSeasonOverview(currentSeason.id).subscribe({
        next: (ov) => {
          this.paidCotisations = ov.totalPaid;
          this.unpaidCotisations = ov.totalUnpaid;
        }
      });

      this.matchService.getMatchesBySeason(currentSeason.id).subscribe({
        next: (matches) => {
          if (matches && matches.length > 0) {
            this.nextMatch = matches[0];
          }
        }
      });
    }
  }
}
