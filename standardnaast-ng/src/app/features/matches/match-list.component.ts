import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { TableModule, TableLazyLoadEvent } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { DropdownModule } from 'primeng/dropdown';
import { DialogModule } from 'primeng/dialog';
import { TagModule } from 'primeng/tag';
import { ConfirmationService, MessageService } from 'primeng/api';
import { MatchService } from '../../core/services/match.service';
import { SeasonService } from '../../core/services/season.service';
import { TeamService } from '../../core/services/team.service';
import { Match, MatchCreateUpdate, Place, CompetitionType, MatchType, PriceType } from '../../core/models/match.model';
import { Team } from '../../core/models/team.model';

@Component({
  selector: 'app-match-list',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    TableModule,
    ButtonModule,
    InputTextModule,
    DropdownModule,
    DialogModule,
    TagModule
  ],
  template: `
    <div class="matches-page flex flex-column gap-2">
      <div class="flex flex-column sm:flex-row justify-content-between align-items-start sm:align-items-center gap-2 bg-white px-3 py-2 border-round-xl border-1 border-200 shadow-1">
        <div>
          <h1 class="text-xl font-bold text-900 m-0">Calendrier des Matchs</h1>
          <p class="text-500 text-xs m-0 mt-1">Programme des rencontres à domicile et à l'extérieur</p>
        </div>
        <button pButton label="Nouveau Match" icon="pi pi-plus" class="p-button-danger p-button-sm font-bold" (click)="openNewDialog()"></button>
      </div>

      <div class="surface-card p-2 sm:p-3 border-round-xl border-1 border-200 shadow-1">
        <p-table
          [value]="matches"
          [lazy]="true"
          (onLazyLoad)="loadMatches($event)"
          [paginator]="true"
          [rows]="pageSize"
          [totalRecords]="totalElements"
          [loading]="loading"
          responsiveLayout="stack"
          styleClass="p-datatable-sm p-datatable-striped">
          <ng-template pTemplate="header">
            <tr>
              <th>Date & Heure</th>
              <th>Adversaire</th>
              <th>Lieu</th>
              <th>Compétition</th>
              <th>Tarif</th>
              <th class="text-center">Actions</th>
            </tr>
          </ng-template>
          <ng-template pTemplate="body" let-match>
            <tr>
              <td><span class="font-bold">{{ match.dateMatch | date:'dd/MM/yyyy HH:mm' }}</span></td>
              <td><span class="font-semibold text-900">{{ match.opponentName }}</span></td>
              <td>
                <p-tag [severity]="match.place === 'HOME' ? 'danger' : 'info'" [value]="match.place === 'HOME' ? 'Domicile' : 'Extérieur'"></p-tag>
              </td>
              <td>{{ match.competitionType }}</td>
              <td>
                <p-tag [severity]="match.priceType === 'TOP' ? 'warning' : 'secondary'" [value]="match.priceType"></p-tag>
              </td>
              <td class="text-center">
                <div class="flex justify-content-center gap-2">
                  <button pButton icon="pi pi-pencil" class="p-button-rounded p-button-text p-button-sm p-button-warning" (click)="openEditDialog(match)"></button>
                  <button pButton icon="pi pi-trash" class="p-button-rounded p-button-text p-button-sm p-button-danger" (click)="confirmDelete(match)"></button>
                </div>
              </td>
            </tr>
          </ng-template>
        </p-table>
      </div>

      <!-- Match Dialog -->
      <p-dialog [(visible)]="dialogVisible" [header]="isEditMode ? 'Modifier le match' : 'Nouveau match'" [modal]="true" [style]="{ width: '550px' }">
        <form [formGroup]="form" (ngSubmit)="saveMatch()" class="flex flex-column gap-3 mt-2">
          <div class="grid">
            <div class="col-12 md:col-6 flex flex-column gap-2">
              <label class="font-semibold text-sm">Saison *</label>
              <input type="text" pInputText formControlName="seasonId" />
            </div>
            <div class="col-12 md:col-6 flex flex-column gap-2">
              <label class="font-semibold text-sm">Adversaire *</label>
              <p-dropdown [options]="teams" optionLabel="name" optionValue="id" formControlName="opponentId" placeholder="Sélectionner une équipe"></p-dropdown>
            </div>
          </div>

          <div class="grid">
            <div class="col-12 md:col-6 flex flex-column gap-2">
              <label class="font-semibold text-sm">Date et heure *</label>
              <input type="datetime-local" pInputText formControlName="dateMatch" />
            </div>
            <div class="col-12 md:col-6 flex flex-column gap-2">
              <label class="font-semibold text-sm">Lieu *</label>
              <p-dropdown [options]="placeOptions" formControlName="place"></p-dropdown>
            </div>
          </div>

          <div class="grid">
            <div class="col-12 md:col-4 flex flex-column gap-2">
              <label class="font-semibold text-sm">Compétition</label>
              <p-dropdown [options]="competitionOptions" formControlName="competitionType"></p-dropdown>
            </div>
            <div class="col-12 md:col-4 flex flex-column gap-2">
              <label class="font-semibold text-sm">Type Match</label>
              <p-dropdown [options]="matchTypeOptions" formControlName="matchType"></p-dropdown>
            </div>
            <div class="col-12 md:col-4 flex flex-column gap-2">
              <label class="font-semibold text-sm">Tarif</label>
              <p-dropdown [options]="priceTypeOptions" formControlName="priceType"></p-dropdown>
            </div>
          </div>

          <div class="flex justify-content-end gap-2 mt-4">
            <button pButton type="button" label="Annuler" icon="pi pi-times" [text]="true" (click)="dialogVisible = false"></button>
            <button pButton type="submit" label="Enregistrer" icon="pi pi-check" class="p-button-danger font-bold"></button>
          </div>
        </form>
      </p-dialog>
    </div>
  `
})
export class MatchListComponent implements OnInit {
  private matchService = inject(MatchService);
  private seasonService = inject(SeasonService);
  private teamService = inject(TeamService);
  private messageService = inject(MessageService);
  private confirmationService = inject(ConfirmationService);
  private fb = inject(FormBuilder);

  matches: Match[] = [];
  teams: Team[] = [];
  totalElements = 0;
  pageSize = 20;
  loading = false;
  dialogVisible = false;
  isEditMode = false;
  selectedMatchId: number | null = null;

  placeOptions = [
    { label: 'Domicile', value: Place.HOME },
    { label: 'Extérieur', value: Place.AWAY }
  ];

  competitionOptions = [
    { label: 'Championnat', value: CompetitionType.CHAMPIONSHIP },
    { label: 'Coupe', value: CompetitionType.CUP },
    { label: 'Champions League', value: CompetitionType.CHAMPIONS_LEAGUE },
    { label: 'Europa League', value: CompetitionType.EUROPA_LEAGUE },
    { label: 'Conference League', value: CompetitionType.CONFERENCE_LEAGUE },
    { label: 'Amical', value: CompetitionType.FRIENDLY }
  ];

  matchTypeOptions = [
    { label: 'Match de groupe', value: MatchType.GROUP_MATCH },
    { label: '16ème de finale', value: MatchType.SIXTEENTH_OF_FINAL },
    { label: '8ème de finale', value: MatchType.EIGHTH_OF_FINAL },
    { label: 'Quart de finale', value: MatchType.QUARTER_FINAL },
    { label: 'Demi-finale', value: MatchType.SEMI_FINAL },
    { label: 'Finale', value: MatchType.FINAL },
    { label: 'Playoffs 1', value: MatchType.PLAYOFFS_1 },
    { label: 'Playoffs 2', value: MatchType.PLAYOFFS_2 }
  ];

  priceTypeOptions = [
    { label: 'Normal', value: PriceType.NORMAL },
    { label: 'Top Match', value: PriceType.TOP }
  ];

  form: FormGroup = this.fb.group({
    seasonId: ['', Validators.required],
    opponentId: [null, Validators.required],
    dateMatch: ['', Validators.required],
    place: [Place.HOME, Validators.required],
    competitionType: [CompetitionType.CHAMPIONSHIP],
    matchType: [MatchType.GROUP_MATCH],
    priceType: [PriceType.NORMAL]
  });

  ngOnInit(): void {
    this.teamService.getAllTeams().subscribe({
      next: (teams) => this.teams = teams
    });
  }

  loadMatches(event: TableLazyLoadEvent): void {
    this.loading = true;
    const page = event.first ? Math.floor(event.first / (event.rows || this.pageSize)) : 0;
    const size = event.rows || this.pageSize;
    const currentSeason = this.seasonService.selectedSeason()?.id;

    this.matchService.getMatches(currentSeason, undefined, undefined, page, size).subscribe({
      next: (res) => {
        this.matches = res.content;
        this.totalElements = res.totalElements;
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  openNewDialog(): void {
    this.isEditMode = false;
    this.selectedMatchId = null;
    const curSeason = this.seasonService.selectedSeason()?.id || '';
    this.form.reset({
      seasonId: curSeason,
      place: Place.HOME,
      competitionType: CompetitionType.CHAMPIONSHIP,
      matchType: MatchType.GROUP_MATCH,
      priceType: PriceType.NORMAL
    });
    this.dialogVisible = true;
  }

  openEditDialog(match: Match): void {
    this.isEditMode = true;
    this.selectedMatchId = match.id;
    this.form.patchValue({
      seasonId: match.seasonId,
      opponentId: match.opponentId,
      dateMatch: match.dateMatch,
      place: match.place,
      competitionType: match.competitionType,
      matchType: match.matchType,
      priceType: match.priceType
    });
    this.dialogVisible = true;
  }

  saveMatch(): void {
    if (this.form.invalid) return;
    const val = this.form.value as MatchCreateUpdate;

    if (this.isEditMode && this.selectedMatchId) {
      this.matchService.updateMatch(this.selectedMatchId, val).subscribe({
        next: () => {
          this.dialogVisible = false;
          this.messageService.add({ severity: 'success', summary: 'Succès', detail: 'Match modifié' });
          this.loadMatches({ first: 0, rows: this.pageSize });
        }
      });
    } else {
      this.matchService.createMatch(val).subscribe({
        next: () => {
          this.dialogVisible = false;
          this.messageService.add({ severity: 'success', summary: 'Succès', detail: 'Match créé' });
          this.loadMatches({ first: 0, rows: this.pageSize });
        }
      });
    }
  }

  confirmDelete(match: Match): void {
    this.confirmationService.confirm({
      message: `Supprimer le match contre ${match.opponentName} ?`,
      acceptButtonStyleClass: 'p-button-danger',
      accept: () => {
        this.matchService.deleteMatch(match.id).subscribe({
          next: () => {
            this.messageService.add({ severity: 'success', summary: 'Supprimé', detail: 'Match supprimé' });
            this.loadMatches({ first: 0, rows: this.pageSize });
          }
        });
      }
    });
  }
}
