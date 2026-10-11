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
import { IconFieldModule } from 'primeng/iconfield';
import { InputIconModule } from 'primeng/inputicon';
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
    TooltipModule,
    IconFieldModule,
    InputIconModule
  ],
  template: `
    <div class="cotisations-container flex flex-column gap-2">
      <!-- Top Header Card -->
      <div class="surface-card px-3 py-2 border-round-xl border-1 border-200 shadow-1 flex-shrink-0 flex flex-wrap align-items-center justify-content-between gap-3">
        <div class="flex flex-wrap align-items-center gap-3">
          <!-- Saison Selector -->
          <div class="flex align-items-center gap-2">
            <label class="font-semibold text-sm text-700 white-space-nowrap">Saison :</label>
            <p-dropdown
              [options]="seasons"
              optionLabel="id"
              optionValue="id"
              [(ngModel)]="selectedSeasonId"
              (onChange)="onSeasonFilterChange($event.value)"
              placeholder="Choisir..."
              styleClass="p-inputtext-sm"
              [style]="{ 'min-width': '130px' }">
            </p-dropdown>
          </div>

          <!-- Payé -->
          <div class="flex align-items-baseline gap-1">
            <span class="font-medium text-sm text-600">Payé :</span>
            <span class="font-bold text-green-600 text-base">
              {{ overview?.totalPaid ?? 0 }}
            </span>
          </div>

          <!-- Non Payé -->
          <div class="flex align-items-baseline gap-1">
            <span class="font-medium text-sm text-600">Non Payé :</span>
            <span class="font-bold text-red-600 text-base">
              {{ overview?.totalUnpaid ?? 0 }}
            </span>
          </div>

          <!-- Total -->
          <div class="flex align-items-baseline gap-1">
            <span class="font-medium text-sm text-600">Total :</span>
            <span class="font-bold text-900 text-base">
              {{ formattedTotalAmount }}
            </span>
          </div>

          <!-- Filtre Membre -->
          <div class="flex align-items-center">
            <p-iconfield iconPosition="left">
              <p-inputicon class="pi pi-search"></p-inputicon>
              <input
                type="text"
                pInputText
                [(ngModel)]="searchTerm"
                placeholder="Filtrer membre (nom, prénom)..."
                class="p-inputtext-sm w-15rem"
              />
              @if (searchTerm) {
                <p-inputicon class="pi pi-times cursor-pointer text-500" (click)="clearSearch()" pTooltip="Effacer le filtre"></p-inputicon>
              }
            </p-iconfield>
          </div>
        </div>

        <!-- Top Action Buttons -->
        <div class="flex align-items-center gap-2">
          <button
            pButton
            type="button"
            label="Impression cotisations"
            icon="pi pi-print"
            class="p-button-outlined p-button-secondary p-button-sm font-semibold"
            (click)="onPrintCotisations()"
            pTooltip="Imprimer le tableau des cotisations"
            tooltipPosition="bottom">
          </button>
          <button
            pButton
            type="button"
            label="Ajouter cotisation"
            icon="pi pi-plus"
            class="p-button-danger p-button-sm font-bold"
            pTooltip="Enregistrer une cotisation manuellement"
            tooltipPosition="bottom"
            (click)="openNewDialog()">
          </button>
        </div>
      </div>

      <!-- Top Section (50% vertical space): Cotisations payées cartes membres envoyées -->
      <div class="top-table-container flex-1 flex flex-column min-h-0">
        <div class="table-section flex flex-column surface-card border-round-xl border-1 border-200 shadow-1 overflow-hidden h-full">
          <div class="table-header-title text-center font-bold py-1 border-bottom-1 border-200 flex-shrink-0">
            Cotisations payées cartes membres envoyées
          </div>
          <p-table
            [value]="filteredPaidCardSent"
            [loading]="loading"
            [paginator]="true"
            [rows]="10"
            [rowsPerPageOptions]="[10, 20, 50]"
            [showCurrentPageReport]="true"
            currentPageReportTemplate="{first} - {last} sur {totalRecords}"
            [scrollable]="true"
            scrollHeight="flex"
            styleClass="p-datatable-sm p-datatable-striped p-datatable-gridlines h-full flex flex-column">
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
                <td colspan="4" class="text-center p-3 text-500">Aucune cotisation trouvée</td>
              </tr>
            </ng-template>
          </p-table>
        </div>
      </div>

      <!-- Bottom Section (50% vertical space, split horizontally in 2) -->
      <div class="bottom-tables-container flex-1 flex min-h-0">
        <div class="grid m-0 w-full h-full p-0">
          <!-- Bottom Left: Cotisations payées cartes membres non envoyées -->
          <div class="col-12 md:col-6 p-0 pr-0 md:pr-1 h-full flex flex-column min-h-0">
            <div class="table-section flex flex-column surface-card border-round-xl border-1 border-200 shadow-1 overflow-hidden h-full">
              <div class="table-header-title text-center font-bold py-1 border-bottom-1 border-200 flex-shrink-0">
                Cotisations payées cartes membres non envoyées
              </div>
              <p-table
                [value]="filteredPaidCardNotSent"
                [(selection)]="selectedPaidNotSent"
                dataKey="cotisationId"
                [loading]="loading"
                [paginator]="true"
                [rows]="10"
                [rowsPerPageOptions]="[10, 20, 50]"
                [showCurrentPageReport]="true"
                currentPageReportTemplate="{first} - {last} sur {totalRecords}"
                [scrollable]="true"
                scrollHeight="flex"
                styleClass="p-datatable-sm p-datatable-striped p-datatable-gridlines h-full flex flex-column">
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
                    <td colspan="5" class="text-center p-3 text-500">Aucune cotisation trouvée</td>
                  </tr>
                </ng-template>
              </p-table>
              <!-- Bottom Action for Left Table -->
              <div class="flex justify-content-end p-2 bg-50 border-top-1 border-200 flex-shrink-0">
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
          <div class="col-12 md:col-6 p-0 pl-0 md:pl-1 mt-2 md:mt-0 h-full flex flex-column min-h-0">
            <div class="table-section flex flex-column surface-card border-round-xl border-1 border-200 shadow-1 overflow-hidden h-full">
              <div class="table-header-title text-center font-bold py-1 border-bottom-1 border-200 flex-shrink-0">
                Cotisations non payées
              </div>
              <p-table
                [value]="filteredUnpaidMembers"
                [(selection)]="selectedUnpaid"
                dataKey="memberId"
                [loading]="loading"
                [paginator]="true"
                [rows]="10"
                [rowsPerPageOptions]="[10, 20, 50]"
                [showCurrentPageReport]="true"
                currentPageReportTemplate="{first} - {last} sur {totalRecords}"
                [scrollable]="true"
                scrollHeight="flex"
                styleClass="p-datatable-sm p-datatable-striped p-datatable-gridlines h-full flex flex-column">
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
                    <td colspan="4" class="text-center p-3 text-500">Aucun membre trouvé</td>
                  </tr>
                </ng-template>
              </p-table>
              <!-- Bottom Action for Right Table -->
              <div class="flex justify-content-end p-2 bg-50 border-top-1 border-200 flex-shrink-0">
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
      height: 100%;
      min-height: 550px;
      box-sizing: border-box;
      overflow: hidden;
    }

    .top-table-container,
    .bottom-tables-container {
      flex: 1 1 0;
      min-height: 200px;
    }

    .table-section {
      background: #ffffff;
    }

    .table-header-title {
      background: #f8f9fa;
      color: #374151;
      font-size: 0.875rem;
    }

    :host ::ng-deep .table-section p-table {
      flex: 1 1 0;
      min-height: 0;
      display: flex;
      flex-direction: column;
      height: 100%;
    }

    :host ::ng-deep .table-section .p-datatable {
      display: flex;
      flex-direction: column;
      height: 100%;
      flex: 1 1 0;
      min-height: 0;
    }

    :host ::ng-deep .table-section .p-datatable-wrapper {
      flex: 1 1 0;
      min-height: 0;
      overflow-y: auto;
    }

    :host ::ng-deep .table-section .p-datatable-table {
      width: 100%;
    }

    :host ::ng-deep .table-section .p-datatable-thead > tr > th {
      background: #f8f9fa;
      color: #495057;
      padding: 0.35rem 0.5rem;
      font-size: 0.85rem;
      border-color: #dee2e6;
      position: sticky;
      top: 0;
      z-index: 1;
    }

    :host ::ng-deep .table-section .p-datatable-tbody > tr > td {
      padding: 0.3rem 0.5rem;
      font-size: 0.85rem;
      border-color: #dee2e6;
    }

    :host ::ng-deep .table-section .p-paginator {
      padding: 0.15rem 0.5rem;
      font-size: 0.8rem;
      flex-shrink: 0;
      border-top: 1px solid #dee2e6;
    }

    :host ::ng-deep .table-section .p-paginator .p-paginator-pages .p-paginator-page,
    :host ::ng-deep .table-section .p-paginator .p-paginator-first,
    :host ::ng-deep .table-section .p-paginator .p-paginator-prev,
    :host ::ng-deep .table-section .p-paginator .p-paginator-next,
    :host ::ng-deep .table-section .p-paginator .p-paginator-last {
      min-width: 24px;
      height: 24px;
      font-size: 0.8rem;
      padding: 0;
      margin: 0 1px;
    }

    :host ::ng-deep .table-section .p-paginator .p-paginator-current {
      font-size: 0.8rem;
      padding: 0 0.5rem;
    }

    :host ::ng-deep .table-section .p-paginator .p-dropdown {
      height: 24px;
      font-size: 0.8rem;
    }

    :host ::ng-deep .table-section .p-paginator .p-dropdown .p-dropdown-label {
      padding: 0.05rem 0.35rem;
      font-size: 0.8rem;
      line-height: 20px;
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

  searchTerm = '';

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

  clearSearch(): void {
    this.searchTerm = '';
  }

  filterList(list: MemberCotisationItem[] | undefined): MemberCotisationItem[] {
    if (!list) return [];
    if (!this.searchTerm || !this.searchTerm.trim()) return list;
    const term = this.searchTerm.trim().toLowerCase();
    return list.filter(item => {
      const name = (item.name || '').toLowerCase();
      const firstName = (item.firstName || '').toLowerCase();
      const fullName = `${firstName} ${name}`.trim();
      const reverseName = `${name} ${firstName}`.trim();
      const memberNumber = item.memberNumber != null ? item.memberNumber.toString() : '';

      return (
        name.includes(term) ||
        firstName.includes(term) ||
        fullName.includes(term) ||
        reverseName.includes(term) ||
        memberNumber.includes(term)
      );
    });
  }

  get filteredPaidCardSent(): MemberCotisationItem[] {
    return this.filterList(this.overview?.paidCardSent);
  }

  get filteredPaidCardNotSent(): MemberCotisationItem[] {
    return this.filterList(this.overview?.paidCardNotSent);
  }

  get filteredUnpaidMembers(): MemberCotisationItem[] {
    return this.filterList(this.overview?.unpaidMembers);
  }

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
    const seasonId = this.selectedSeasonId || this.seasonService.selectedSeason()?.id;
    if (!seasonId) {
      this.messageService.add({
        severity: 'warn',
        summary: 'Attention',
        detail: 'Veuillez sélectionner une saison pour imprimer les cotisations.'
      });
      return;
    }

    this.messageService.add({
      severity: 'info',
      summary: 'Impression cotisations',
      detail: 'Génération du tableau des cotisations en cours...'
    });

    this.cotisationService.downloadCotisationsPdf(seasonId).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const fileName = `Tableau_Cotisations_${seasonId}.pdf`;

        // Download the PDF file
        const link = document.createElement('a');
        link.href = url;
        link.download = fileName;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);

        // Open in new tab for print/preview
        window.open(url, '_blank');

        this.messageService.add({
          severity: 'success',
          summary: 'Succès',
          detail: `Tableau des cotisations (${fileName}) téléchargé avec succès`
        });
      },
      error: () => {
        this.messageService.add({
          severity: 'error',
          summary: 'Erreur',
          detail: 'Échec de la génération du tableau des cotisations'
        });
      }
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
