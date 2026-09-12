import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { TableModule } from 'primeng/table';
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
import { Member } from '../../core/models/member.model';

@Component({
  selector: 'app-cotisation-list',
  standalone: true,
  imports: [
    CommonModule,
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
        <div class="flex gap-2">
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
          [value]="allPaidCotisations"
          [(selection)]="selectedCotisations"
          [loading]="loading"
          responsiveLayout="stack"
          styleClass="p-datatable-sm p-datatable-striped">
          <ng-template pTemplate="header">
            <tr>
              <th style="width: 4rem"><p-tableHeaderCheckbox></p-tableHeaderCheckbox></th>
              <th>N° Membre</th>
              <th>Membre</th>
              <th>Date de paiement</th>
              <th>Carte envoyée</th>
              <th class="text-center">Actions</th>
            </tr>
          </ng-template>
          <ng-template pTemplate="body" let-cot>
            <tr>
              <td><p-tableCheckbox [value]="cot"></p-tableCheckbox></td>
              <td><span class="font-bold text-red-700">{{ cot.memberNumber || '-' }}</span></td>
              <td><span class="font-semibold">{{ cot.firstName }} {{ cot.lastName }}</span></td>
              <td>{{ cot.datePaiement ? (cot.datePaiement | date:'dd/MM/yyyy') : '-' }}</td>
              <td>
                <p-tag [severity]="cot.carteMembreEnvoyee ? 'success' : 'warning'" [value]="cot.carteMembreEnvoyee ? 'Envoyée' : 'Non envoyée'"></p-tag>
              </td>
              <td class="text-center">
                <div class="flex justify-content-center gap-2">
                  <button pButton icon="pi pi-trash" class="p-button-rounded p-button-text p-button-sm p-button-danger" (click)="confirmDelete(cot)"></button>
                </div>
              </td>
            </tr>
          </ng-template>
          <ng-template pTemplate="emptymessage">
            <tr><td colspan="6" class="text-center p-3 text-500">Aucune cotisation enregistrée pour cette saison.</td></tr>
          </ng-template>
        </p-table>
      </div>

      <!-- Cotisation Dialog -->
      <p-dialog [(visible)]="dialogVisible" header="Enregistrer une cotisation" [modal]="true" [style]="{ width: '450px' }">
        <form [formGroup]="form" (ngSubmit)="saveCotisation()" class="flex flex-column gap-3 mt-2">
          <div class="flex flex-column gap-2">
            <label class="font-semibold text-sm">Membre *</label>
            <p-dropdown [options]="members" optionLabel="name" optionValue="id" formControlName="memberId" [filter]="true" placeholder="Choisir un membre"></p-dropdown>
          </div>

          <div class="flex flex-column gap-2">
            <label class="font-semibold text-sm">Saison *</label>
            <input type="text" pInputText formControlName="seasonId" />
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
  private cotisationService = inject(CotisationService);
  private seasonService = inject(SeasonService);
  private memberService = inject(MemberService);
  private messageService = inject(MessageService);
  private confirmationService = inject(ConfirmationService);
  private fb = inject(FormBuilder);

  overview: CotisationsSeasonOverview | null = null;
  allPaidCotisations: PersonCotisation[] = [];
  selectedCotisations: PersonCotisation[] = [];
  members: Member[] = [];
  loading = false;
  dialogVisible = false;

  form: FormGroup = this.fb.group({
    memberId: [null, Validators.required],
    seasonId: ['', Validators.required],
    datePaiement: [''],
    carteMembreEnvoyee: [false]
  });

  ngOnInit(): void {
    this.memberService.getMembers(undefined, 0, 1000).subscribe({
      next: (res) => this.members = res.content
    });
    this.loadCotisations();
  }

  loadCotisations(): void {
    const curSeason = this.seasonService.selectedSeason()?.id;
    if (!curSeason) return;

    this.loading = true;
    this.cotisationService.getSeasonOverview(curSeason).subscribe({
      next: (data) => {
        this.overview = data;
        this.allPaidCotisations = [...(data.paidCardSent || []), ...(data.paidCardNotSent || [])];
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  openNewDialog(): void {
    const curSeason = this.seasonService.selectedSeason()?.id || '';
    this.form.reset({
      seasonId: curSeason,
      carteMembreEnvoyee: false
    });
    this.dialogVisible = true;
  }

  saveCotisation(): void {
    if (this.form.invalid) return;

    this.cotisationService.registerMemberCotisation(this.form.value).subscribe({
      next: () => {
        this.dialogVisible = false;
        this.messageService.add({ severity: 'success', summary: 'Succès', detail: 'Cotisation enregistrée' });
        this.loadCotisations();
      }
    });
  }

  bulkMarkSent(): void {
    const ids = this.selectedCotisations.map(c => c.id);
    if (!ids.length) return;

    this.cotisationService.bulkUpdateCardSent(ids, true).subscribe({
      next: () => {
        this.messageService.add({ severity: 'success', summary: 'Succès', detail: 'Cartes marquées comme envoyées' });
        this.selectedCotisations = [];
        this.loadCotisations();
      }
    });
  }

  confirmDelete(cot: PersonCotisation): void {
    this.confirmationService.confirm({
      message: `Supprimer la cotisation pour ${cot.firstName} ${cot.lastName} ?`,
      acceptButtonStyleClass: 'p-button-danger',
      accept: () => {
        this.cotisationService.deleteMemberCotisation(cot.id).subscribe({
          next: () => {
            this.messageService.add({ severity: 'success', summary: 'Supprimé', detail: 'Cotisation supprimée' });
            this.loadCotisations();
          }
        });
      }
    });
  }
}
