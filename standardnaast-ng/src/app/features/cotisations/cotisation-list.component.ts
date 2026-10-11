import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { DropdownModule } from 'primeng/dropdown';
import { DialogModule } from 'primeng/dialog';
import { CheckboxModule } from 'primeng/checkbox';
import { TagModule } from 'primeng/tag';
import { TooltipModule } from 'primeng/tooltip';
import { ConfirmationService, MessageService } from 'primeng/api';
import { forkJoin } from 'rxjs';
import { CotisationService } from '../../core/services/cotisation.service';
import { SeasonService } from '../../core/services/season.service';
import { MemberService } from '../../core/services/member.service';
import { CotisationsSeasonOverview, MemberCotisationItem } from '../../core/models/cotisation.model';
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
    TooltipModule
  ],
  template: `
    <div class="cotisations-container flex flex-column gap-2 p-2">
      <!-- Top Red Header Bar -->
      <div class="top-red-bar flex flex-wrap align-items-center justify-content-between px-3 py-2 border-round shadow-2">
        <div class="flex flex-wrap align-items-center gap-3">
          <!-- Saison Selector -->
          <div class="flex align-items-center gap-2">
            <span class="text-white font-bold text-sm">Saison</span>
            <p-dropdown
              [options]="seasons"
              optionLabel="id"
              optionValue="id"
              [(ngModel)]="selectedSeasonId"
              (onChange)="onSeasonFilterChange($event.value)"
              placeholder="Choisir..."
              styleClass="p-inputtext-sm season-dropdown"
              [style]="{ 'min-width': '140px' }">
            </p-dropdown>
          </div>

          <!-- Payé -->
          <div class="flex align-items-center gap-2">
            <span class="text-white font-bold text-sm">Payé</span>
            <input
              type="text"
              pInputText
              [value]="overview?.totalPaid ?? 0"
              readonly
              class="p-inputtext-sm stat-input font-bold"
            />
          </div>

          <!-- Non Payé -->
          <div class="flex align-items-center gap-2">
            <span class="text-white font-bold text-sm">Non Payé</span>
            <input
              type="text"
              pInputText
              [value]="overview?.totalUnpaid ?? 0"
              readonly
              class="p-inputtext-sm stat-input font-bold"
            />
          </div>

          <!-- Total -->
          <div class="flex align-items-center gap-2">
            <span class="text-white font-bold text-sm">Total</span>
            <input
              type="text"
              pInputText
              [value]="formattedTotalAmount"
              readonly
              class="p-inputtext-sm stat-input-wide font-bold"
            />
          </div>
        </div>

        <!-- Top Action Buttons -->
        <div class="flex align-items-center gap-2 mt-2 sm:mt-0">
          <button
            pButton
            type="button"
            label="Impression cotisations"
            icon="pi pi-print"
            class="p-button-secondary p-button-sm font-bold bg-white text-900 border-none shadow-1 hover:surface-200"
            (click)="onPrintCotisations()"
            pTooltip="Imprimer le tableau des cotisations"
            tooltipPosition="bottom">
          </button>
          <button
            pButton
            type="button"
            icon="pi pi-plus"
            pTooltip="Enregistrer une cotisation manuellement"
            tooltipPosition="bottom"
            class="p-button-secondary p-button-sm bg-white text-900 border-none shadow-1 hover:surface-200"
            (click)="openNewDialog()">
          </button>
        </div>
      </div>

      <!-- Top Table: Cotisations payées cartes membres envoyées -->
      <div class="table-section flex flex-column surface-card border-round shadow-1 overflow-hidden">
        <div class="table-header-title text-center font-bold py-1 border-bottom-1 border-300">
          Cotisations payées cartes membres envoyées
        </div>
        <p-table
          [value]="overview?.paidCardSent || []"
          [loading]="loading"
          [scrollable]="true"
          scrollHeight="18vh"
          styleClass="p-datatable-sm p-datatable-striped p-datatable-gridlines compact-table">
          <ng-template pTemplate="header">
            <tr>
              <th style="width: 20%">Numéro membre</th>
              <th style="width: 30%">Nom</th>
              <th style="width: 25%">Prénom</th>
              <th style="width: 25%">Date paiement</th>
            </tr>
          </ng-template>
          <ng-template pTemplate="body" let-cot>
            <tr>
              <td>{{ cot.memberNumber }}</td>
              <td>{{ cot.name }}</td>
              <td>{{ cot.firstName }}</td>
              <td>{{ cot.paymentDate }}</td>
            </tr>
          </ng-template>
          <ng-template pTemplate="emptymessage">
            <tr>
              <td colspan="4" class="text-center p-3 text-500">No content in table</td>
            </tr>
          </ng-template>
        </p-table>
      </div>

      <!-- Bottom Two Tables (Split 50% / 50%) -->
      <div class="bottom-tables-grid grid m-0">
        <!-- Bottom Left: Cotisations payées cartes membres non envoyées -->
        <div class="col-12 md:col-6 p-0 pr-0 md:pr-1">
          <div class="table-section flex flex-column surface-card border-round shadow-1 overflow-hidden h-full">
            <div class="table-header-title text-center font-bold py-1 border-bottom-1 border-300">
              Cotisations payées cartes membres non envoyées
            </div>
            <p-table
              [value]="overview?.paidCardNotSent || []"
              [(selection)]="selectedPaidNotSent"
              dataKey="cotisationId"
              [loading]="loading"
              [scrollable]="true"
              scrollHeight="22vh"
              styleClass="p-datatable-sm p-datatable-striped p-datatable-gridlines compact-table">
              <ng-template pTemplate="header">
                <tr>
                  <th style="width: 3rem"><p-tableHeaderCheckbox></p-tableHeaderCheckbox></th>
                  <th style="width: 20%">Numéro membre</th>
                  <th style="width: 30%">Nom</th>
                  <th style="width: 25%">Prénom</th>
                  <th style="width: 25%">Date paiement</th>
                </tr>
              </ng-template>
              <ng-template pTemplate="body" let-cot>
                <tr [pSelectableRow]="cot">
                  <td><p-tableCheckbox [value]="cot"></p-tableCheckbox></td>
                  <td>{{ cot.memberNumber }}</td>
                  <td>{{ cot.name }}</td>
                  <td>{{ cot.firstName }}</td>
                  <td>{{ cot.paymentDate }}</td>
                </tr>
              </ng-template>
              <ng-template pTemplate="emptymessage">
                <tr>
                  <td colspan="5" class="text-center p-3 text-500">No content in table</td>
                </tr>
              </ng-template>
            </p-table>
            <!-- Bottom Action for Left Table -->
            <div class="flex justify-content-end p-2 bg-50 border-top-1 border-200">
              <button
                pButton
                type="button"
                label="Cartes de membres envoyées"
                class="p-button-secondary p-button-sm border-1 border-300"
                [disabled]="!selectedPaidNotSent.length"
                (click)="onMarkCardsSent()">
              </button>
            </div>
          </div>
        </div>

        <!-- Bottom Right: Cotisations non payées -->
        <div class="col-12 md:col-6 p-0 pl-0 md:pl-1 mt-2 md:mt-0">
          <div class="table-section flex flex-column surface-card border-round shadow-1 overflow-hidden h-full">
            <div class="table-header-title text-center font-bold py-1 border-bottom-1 border-300">
              Cotisations non payées
            </div>
            <p-table
              [value]="overview?.unpaidMembers || []"
              [(selection)]="selectedUnpaid"
              dataKey="memberId"
              [loading]="loading"
              [scrollable]="true"
              scrollHeight="22vh"
              styleClass="p-datatable-sm p-datatable-striped p-datatable-gridlines compact-table">
              <ng-template pTemplate="header">
                <tr>
                  <th style="width: 3rem"><p-tableHeaderCheckbox></p-tableHeaderCheckbox></th>
                  <th style="width: 25%">Numéro membre</th>
                  <th style="width: 40%">Nom</th>
                  <th style="width: 35%">Prénom</th>
                </tr>
              </ng-template>
              <ng-template pTemplate="body" let-cot>
                <tr [pSelectableRow]="cot">
                  <td><p-tableCheckbox [value]="cot"></p-tableCheckbox></td>
                  <td>{{ cot.memberNumber }}</td>
                  <td>{{ cot.name }}</td>
                  <td>{{ cot.firstName }}</td>
                </tr>
              </ng-template>
              <ng-template pTemplate="emptymessage">
                <tr>
                  <td colspan="4" class="text-center p-3 text-500">No content in table</td>
                </tr>
              </ng-template>
            </p-table>
            <!-- Bottom Action for Right Table -->
            <div class="flex justify-content-end p-2 bg-50 border-top-1 border-200">
              <button
                pButton
                type="button"
                label="Cotisation payée"
                class="p-button-secondary p-button-sm border-1 border-300"
                [disabled]="!selectedUnpaid.length"
                (click)="onMarkCotisationPaid()">
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Cotisation Dialog (for manual addition if needed) -->
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
  `,
  styles: [`
    .cotisations-container {
      height: calc(100vh - 75px);
      max-height: calc(100vh - 75px);
      box-sizing: border-box;
      overflow-y: hidden;
    }

    .top-red-bar {
      background-color: #8B0000;
      color: #ffffff;
      min-height: 48px;
    }

    .stat-input {
      width: 70px;
      text-align: center;
      background-color: #ffffff !important;
      color: #000000 !important;
      opacity: 1 !important;
      cursor: default;
    }

    .stat-input-wide {
      width: 100px;
      text-align: center;
      background-color: #ffffff !important;
      color: #000000 !important;
      opacity: 1 !important;
      cursor: default;
    }

    .table-section {
      background: #ffffff;
      border: 1px solid #ced4da;
    }

    .table-header-title {
      background: linear-gradient(to bottom, #f2f2f2, #e0e0e0);
      color: #333333;
      font-size: 0.9rem;
      border-bottom: 1px solid #cccccc;
    }

    :host ::ng-deep .compact-table .p-datatable-thead > tr > th {
      background: #f8f9fa;
      color: #495057;
      padding: 0.4rem 0.5rem;
      font-size: 0.85rem;
      border-color: #dee2e6;
    }

    :host ::ng-deep .compact-table .p-datatable-tbody > tr > td {
      padding: 0.35rem 0.5rem;
      font-size: 0.85rem;
      border-color: #dee2e6;
    }

    .bottom-tables-grid {
      flex: 1 1 auto;
      min-height: 0;
    }
  `]
})
export class CotisationListComponent implements OnInit {
  private cotisationService = inject(CotisationService);
  private seasonService = inject(SeasonService);
  private memberService = inject(MemberService);
  private messageService = inject(MessageService);
  private confirmationService = inject(ConfirmationService);
  private fb = inject(FormBuilder);

  overview: CotisationsSeasonOverview | null = null;
  selectedPaidNotSent: MemberCotisationItem[] = [];
  selectedUnpaid: MemberCotisationItem[] = [];

  members: (Member & { displayName?: string })[] = [];
  seasons: Season[] = [];
  selectedSeasonId: string | null = null;
  loading = false;
  dialogVisible = false;

  form: FormGroup = this.fb.group({
    memberId: [null, Validators.required],
    seasonId: ['', Validators.required],
    datePaiement: [''],
    carteMembreEnvoyee: [false]
  });

  get formattedTotalAmount(): string {
    if (this.overview?.totalAmount != null) {
      return `${this.overview.totalAmount} €`;
    }
    const totalPaid = this.overview?.totalPaid ?? 0;
    const currentSeason = this.seasons.find(s => s.id === this.selectedSeasonId);
    const amount = currentSeason?.montantCotisation ?? this.overview?.cotisationAmount ?? 0;
    return `${totalPaid * amount} €`;
  }

  ngOnInit(): void {
    const currentSeason = this.seasonService.selectedSeason()?.id || null;
    this.selectedSeasonId = currentSeason;

    this.seasonService.getAllSeasons().subscribe({
      next: (seasons) => {
        this.seasons = seasons;
        if (!this.selectedSeasonId && seasons.length > 0) {
          this.selectedSeasonId = seasons[0].id;
        }
        this.loadOverview();
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
  }

  onSeasonFilterChange(seasonId: string | null): void {
    this.selectedSeasonId = seasonId;
    this.selectedPaidNotSent = [];
    this.selectedUnpaid = [];
    this.loadOverview();
  }

  loadOverview(): void {
    const curSeason = this.selectedSeasonId || this.seasonService.selectedSeason()?.id;
    if (!curSeason) {
      this.overview = null;
      return;
    }

    this.loading = true;
    this.cotisationService.getSeasonOverview(curSeason).subscribe({
      next: (data) => {
        this.overview = data;
        this.loading = false;
      },
      error: () => {
        this.overview = null;
        this.loading = false;
      }
    });
  }

  onMarkCardsSent(): void {
    if (!this.selectedPaidNotSent.length) return;

    const ids = this.selectedPaidNotSent
      .map(c => c.cotisationId)
      .filter((id): id is number => id != null);

    if (!ids.length) return;

    this.cotisationService.bulkUpdateCardSent(ids, true).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Succès',
          detail: 'Cartes de membres marquées comme envoyées'
        });
        this.selectedPaidNotSent = [];
        this.loadOverview();
      },
      error: () => {
        this.messageService.add({
          severity: 'error',
          summary: 'Erreur',
          detail: 'Impossible de mettre à jour le statut des cartes'
        });
      }
    });
  }

  onMarkCotisationPaid(): void {
    if (!this.selectedUnpaid.length || !this.selectedSeasonId) return;

    const today = new Date().toISOString().split('T')[0];
    const calls = this.selectedUnpaid.map(item =>
      this.cotisationService.registerMemberCotisation({
        memberId: item.memberId,
        seasonId: this.selectedSeasonId!,
        datePaiement: today,
        carteMembreEnvoyee: false
      })
    );

    this.loading = true;
    forkJoin(calls).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Succès',
          detail: 'Cotisations enregistrées avec succès'
        });
        this.selectedUnpaid = [];
        this.loadOverview();
      },
      error: () => {
        this.loading = false;
        this.messageService.add({
          severity: 'error',
          summary: 'Erreur',
          detail: "Erreur lors de l'enregistrement des cotisations"
        });
      }
    });
  }

  onPrintCotisations(): void {
    this.messageService.add({
      severity: 'info',
      summary: 'Impression cotisations',
      detail: 'Fonctionnalité disponible prochainement'
    });
  }

  openNewDialog(): void {
    const curSeason = this.selectedSeasonId || this.seasonService.selectedSeason()?.id || (this.seasons.length > 0 ? this.seasons[0].id : '');
    const today = new Date().toISOString().split('T')[0];
    this.form.reset({
      seasonId: curSeason,
      datePaiement: today,
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
        this.loadOverview();
      },
      error: () => {
        this.messageService.add({ severity: 'error', summary: 'Erreur', detail: "Impossible d'enregistrer la cotisation" });
      }
    });
  }
}
