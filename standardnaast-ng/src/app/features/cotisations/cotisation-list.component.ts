import { Component, OnInit, ViewChild, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Table, TableModule, TableLazyLoadEvent } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { DropdownModule } from 'primeng/dropdown';
import { DialogModule } from 'primeng/dialog';
import { CheckboxModule } from 'primeng/checkbox';
import { TagModule } from 'primeng/tag';
import { CardModule } from 'primeng/card';
import { ConfirmationService, MessageService } from 'primeng/api';
import { CotisationService } from '../../core/services/cotisation.service';
import { SeasonService } from '../../core/services/season.service';
import { MemberService } from '../../core/services/member.service';
import { CotisationsSeasonOverview, PersonCotisation } from '../../core/models/cotisation.model';
import { Season } from '../../core/models/season.model';
import { Member } from '../../core/models/member.model';

@Component({
  selector: 'app-cotisation-list',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    TableModule,
    ButtonModule,
    InputTextModule,
    DropdownModule,
    DialogModule,
    CheckboxModule,
    TagModule,
    CardModule
  ],
  template: `
    <div class="cotisations-page flex flex-column gap-2">
      <div class="flex flex-column sm:flex-row justify-content-between align-items-start sm:align-items-center gap-2 bg-white px-3 py-2 border-round-xl border-1 border-200 shadow-1">
        <div>
          <h1 class="text-xl font-bold text-900 m-0">Gestion des Cotisations</h1>
          <p class="text-500 text-xs m-0 mt-1">Suivi des cotisations annuelles, paiements et expéditions des cartes</p>
        </div>
        <div class="flex flex-wrap align-items-center gap-2">
          <div class="flex align-items-center gap-2">
            <label class="font-semibold text-sm text-700 white-space-nowrap">Saison :</label>
            <p-dropdown
              [options]="seasons"
              optionLabel="id"
              optionValue="id"
              [(ngModel)]="selectedSeasonId"
              (onChange)="onSeasonFilterChange($event.value)"
              placeholder="Toutes les saisons"
              [showClear]="true"
              styleClass="p-inputtext-sm"
              [style]="{ 'min-width': '160px' }">
            </p-dropdown>
          </div>
          <button pButton label="Marquer cartes envoyées" icon="pi pi-send" class="p-button-outlined p-button-success p-button-sm font-bold" (click)="bulkMarkSent()" [disabled]="!selectedCotisations.length"></button>
          <button pButton label="Enregistrer Cotisation" icon="pi pi-plus" class="p-button-danger p-button-sm font-bold" (click)="openNewDialog()"></button>
        </div>
      </div>

      <!-- KPI Summary Cards -->
      <div class="grid" *ngIf="overview">
        <div class="col-12 sm:col-4">
          <div class="surface-card p-3 border-round-xl border-1 border-200 shadow-1">
            <span class="text-500 font-medium text-sm">Total Membres</span>
            <div class="text-900 font-bold text-2xl mt-1">{{ overview.totalMembers }}</div>
          </div>
        </div>
        <div class="col-12 sm:col-4">
          <div class="surface-card p-3 border-round-xl border-1 border-200 shadow-1">
            <span class="text-500 font-medium text-green-600 text-sm">Cotisations Payées</span>
            <div class="text-green-600 font-bold text-2xl mt-1">{{ overview.totalPaid }}</div>
          </div>
        </div>
        <div class="col-12 sm:col-4">
          <div class="surface-card p-3 border-round-xl border-1 border-200 shadow-1">
            <span class="text-500 font-medium text-red-600 text-sm">En Attente de Paiement</span>
            <div class="text-red-600 font-bold text-2xl mt-1">{{ overview.totalUnpaid }}</div>
          </div>
        </div>
      </div>

      <div class="surface-card p-2 sm:p-3 border-round-xl border-1 border-200 shadow-1">
        <p-table
          #table
          [value]="cotisations"
          [(selection)]="selectedCotisations"
          [lazy]="true"
          (onLazyLoad)="loadCotisations($event)"
          [paginator]="true"
          [rows]="pageSize"
          [totalRecords]="totalElements"
          [loading]="loading"
          sortField="memberNumber"
          [sortOrder]="1"
          [defaultSortOrder]="1"
          responsiveLayout="stack"
          styleClass="p-datatable-sm p-datatable-striped">
          <ng-template pTemplate="header">
            <tr>
              <th style="width: 4rem"><p-tableHeaderCheckbox></p-tableHeaderCheckbox></th>
              <th pSortableColumn="memberNumber">N° Membre <p-sortIcon field="memberNumber"></p-sortIcon></th>
              <th>Membre</th>
              <th>Saison</th>
              <th>Date de paiement</th>
              <th>Carte envoyée</th>
              <th class="text-center">Actions</th>
            </tr>
          </ng-template>
          <ng-template pTemplate="body" let-cot>
            <tr>
              <td><p-tableCheckbox [value]="cot"></p-tableCheckbox></td>
              <td><span class="font-bold text-red-700">{{ cot.memberNumber || '-' }}</span></td>
              <td><span class="font-semibold">{{ cot.firstName }} {{ cot.name || cot.lastName }}</span></td>
              <td><span class="font-medium text-700">{{ cot.seasonId || selectedSeasonId || '-' }}</span></td>
              <td>{{ (cot.paymentDate || cot.datePaiement) ? ((cot.paymentDate || cot.datePaiement) | date:'dd/MM/yyyy') : '-' }}</td>
              <td>
                <p-tag [severity]="(cot.cardSent ?? cot.carteMembreEnvoyee) ? 'success' : 'warning'" [value]="(cot.cardSent ?? cot.carteMembreEnvoyee) ? 'Envoyée' : 'Non envoyée'"></p-tag>
              </td>
              <td class="text-center">
                <div class="flex justify-content-center gap-2">
                  <button pButton icon="pi pi-trash" class="p-button-rounded p-button-text p-button-sm p-button-danger" (click)="confirmDelete(cot)"></button>
                </div>
              </td>
            </tr>
          </ng-template>
          <ng-template pTemplate="emptymessage">
            <tr><td colspan="7" class="text-center p-3 text-500">Aucune cotisation enregistrée pour cette sélection.</td></tr>
          </ng-template>
        </p-table>
      </div>

      <!-- Cotisation Dialog -->
      <p-dialog [(visible)]="dialogVisible" header="Enregistrer une cotisation" [modal]="true" [style]="{ width: '450px' }">
        <form [formGroup]="form" (ngSubmit)="saveCotisation()" class="flex flex-column gap-3 mt-2">
          <div class="flex flex-column gap-2">
            <label class="font-semibold text-sm">Membre *</label>
            <p-dropdown
              [options]="members"
              optionLabel="displayName"
              optionValue="id"
              formControlName="memberId"
              [filter]="true"
              filterBy="name,firstname,displayName"
              placeholder="Choisir un membre"
              [style]="{ width: '100%' }">
            </p-dropdown>
          </div>

          <div class="flex flex-column gap-2">
            <label class="font-semibold text-sm">Saison *</label>
            <p-dropdown
              [options]="seasons"
              optionLabel="id"
              optionValue="id"
              formControlName="seasonId"
              placeholder="Choisir une saison"
              [style]="{ width: '100%' }">
            </p-dropdown>
          </div>

          <div class="flex flex-column gap-2">
            <label class="font-semibold text-sm">Date de paiement</label>
            <input type="date" pInputText formControlName="datePaiement" />
          </div>

          <div class="flex align-items-center gap-2 mt-2">
            <p-checkbox formControlName="carteMembreEnvoyee" [binary]="true" inputId="carteMembreEnvoyee"></p-checkbox>
            <label for="carteMembreEnvoyee" class="text-sm font-medium">Carte membre envoyée</label>
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
export class CotisationListComponent implements OnInit {
  @ViewChild('table') table?: Table;

  private cotisationService = inject(CotisationService);
  private seasonService = inject(SeasonService);
  private memberService = inject(MemberService);
  private messageService = inject(MessageService);
  private confirmationService = inject(ConfirmationService);
  private fb = inject(FormBuilder);

  overview: CotisationsSeasonOverview | null = null;
  cotisations: (PersonCotisation & { name?: string; paymentDate?: string; cardSent?: boolean; cotisationId?: number })[] = [];
  selectedCotisations: (PersonCotisation & { name?: string; paymentDate?: string; cardSent?: boolean; cotisationId?: number })[] = [];
  members: (Member & { displayName?: string })[] = [];
  seasons: Season[] = [];
  selectedSeasonId: string | null = null;
  totalElements = 0;
  pageSize = 20;
  loading = false;
  dialogVisible = false;

  form: FormGroup = this.fb.group({
    memberId: [null, Validators.required],
    seasonId: ['', Validators.required],
    datePaiement: [''],
    carteMembreEnvoyee: [false]
  });

  ngOnInit(): void {
    const currentSeason = this.seasonService.selectedSeason()?.id || null;
    this.selectedSeasonId = currentSeason;

    this.seasonService.getAllSeasons().subscribe({
      next: (seasons) => {
        this.seasons = seasons;
        if (!this.selectedSeasonId && seasons.length > 0) {
          this.selectedSeasonId = seasons[0].id;
          if (this.table) {
            this.table.reset();
          }
        }
      }
    });

    this.memberService.getMembers(undefined, 0, 1000).subscribe({
      next: (res) => {
        this.members = res.content.map(m => ({
          ...m,
          displayName: `${m.name} ${m.firstname}`.trim()
        }));
      }
    });

    this.loadOverview();
  }

  onSeasonFilterChange(seasonId: string | null): void {
    this.selectedSeasonId = seasonId;
    this.selectedCotisations = [];
    this.reload();
  }

  loadOverview(): void {
    const curSeason = this.selectedSeasonId || this.seasonService.selectedSeason()?.id;
    if (!curSeason) {
      this.overview = null;
      return;
    }

    this.cotisationService.getSeasonOverview(curSeason).subscribe({
      next: (data) => {
        this.overview = data;
      },
      error: () => {
        this.overview = null;
      }
    });
  }

  loadCotisations(event: TableLazyLoadEvent): void {
    this.loading = true;
    const page = event.first ? Math.floor(event.first / (event.rows || this.pageSize)) : 0;
    const size = event.rows || this.pageSize;
    const seasonToFilter = this.selectedSeasonId || undefined;

    let sort = 'person.memberNumber,asc';
    if (event.sortField) {
      const field = Array.isArray(event.sortField) ? event.sortField[0] : event.sortField;
      const order = event.sortOrder === -1 ? 'desc' : 'asc';
      const backendField = field === 'memberNumber' ? 'person.memberNumber' : field;
      sort = `${backendField},${order}`;
    }

    this.cotisationService.getMemberCotisations(seasonToFilter, undefined, undefined, page, size, sort).subscribe({
      next: (res) => {
        this.cotisations = res.content;
        this.totalElements = res.totalElements;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  reload(): void {
    this.loadOverview();
    if (this.table) {
      this.table.reset();
    } else {
      this.loadCotisations({ first: 0, rows: this.pageSize });
    }
  }

  openNewDialog(): void {
    const curSeason = this.selectedSeasonId || this.seasonService.selectedSeason()?.id || (this.seasons.length > 0 ? this.seasons[0].id : '');
    this.form.reset({
      seasonId: curSeason,
      carteMembreEnvoyee: false
    });
    this.dialogVisible = true;
  }

  saveCotisation(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.cotisationService.registerMemberCotisation(this.form.value).subscribe({
      next: () => {
        this.dialogVisible = false;
        this.messageService.add({ severity: 'success', summary: 'Succès', detail: 'Cotisation enregistrée' });
        this.reload();
      },
      error: () => {
        this.messageService.add({ severity: 'error', summary: 'Erreur', detail: "Impossible d'enregistrer la cotisation" });
      }
    });
  }

  bulkMarkSent(): void {
    const ids = this.selectedCotisations
      .map(c => c.cotisationId || c.id)
      .filter(id => id != null) as number[];
    if (!ids.length) return;

    this.cotisationService.bulkUpdateCardSent(ids, true).subscribe({
      next: () => {
        this.messageService.add({ severity: 'success', summary: 'Succès', detail: 'Cartes marquées comme envoyées' });
        this.selectedCotisations = [];
        this.reload();
      },
      error: () => {
        this.messageService.add({ severity: 'error', summary: 'Erreur', detail: 'Impossible de mettre à jour le statut des cartes' });
      }
    });
  }

  confirmDelete(cot: any): void {
    const cotId = cot.cotisationId || cot.id;
    const memberName = `${cot.firstName || ''} ${cot.name || cot.lastName || ''}`.trim();
    this.confirmationService.confirm({
      message: `Supprimer la cotisation pour ${memberName || 'ce membre'} ?`,
      header: 'Suppression de la cotisation',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Oui',
      rejectLabel: 'Non',
      acceptButtonStyleClass: 'p-button-danger',
      rejectButtonStyleClass: 'p-button-text',
      accept: () => {
        if (!cotId) return;
        this.cotisationService.deleteMemberCotisation(cotId).subscribe({
          next: () => {
            this.messageService.add({ severity: 'success', summary: 'Supprimé', detail: 'Cotisation supprimée' });
            this.reload();
          },
          error: () => {
            this.messageService.add({ severity: 'error', summary: 'Erreur', detail: 'Impossible de supprimer la cotisation' });
          }
        });
      }
    });
  }
}
