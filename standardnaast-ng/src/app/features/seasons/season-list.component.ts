import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { InputNumberModule } from 'primeng/inputnumber';
import { CheckboxModule } from 'primeng/checkbox';
import { DialogModule } from 'primeng/dialog';
import { TagModule } from 'primeng/tag';
import { ConfirmationService, MessageService } from 'primeng/api';
import { SeasonService } from '../../core/services/season.service';
import { Season, SeasonCreateUpdate } from '../../core/models/season.model';

@Component({
  selector: 'app-season-list',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    TableModule,
    ButtonModule,
    InputTextModule,
    InputNumberModule,
    CheckboxModule,
    DialogModule,
    TagModule
  ],
  template: `
    <div class="seasons-page flex flex-column gap-2">
      <div class="flex flex-column sm:flex-row justify-content-between align-items-start sm:align-items-center gap-2 bg-white px-3 py-2 border-round-xl border-1 border-200 shadow-1">
        <div>
          <h1 class="text-xl font-bold text-900 m-0">Gestion des Saisons</h1>
          <p class="text-500 text-xs m-0 mt-1">Configurez les saisons sportives et leurs paramètres</p>
        </div>
        <button pButton label="Nouvelle Saison" icon="pi pi-plus" class="p-button-danger p-button-sm font-bold" (click)="openNewDialog()"></button>
      </div>

      <div class="surface-card p-2 sm:p-3 border-round-xl border-1 border-200 shadow-1">
        <p-table [value]="seasons" [loading]="loading" responsiveLayout="stack" styleClass="p-datatable-sm p-datatable-striped">
          <ng-template pTemplate="header">
            <tr>
              <th>Identifiant</th>
              <th>Date de début</th>
              <th>Date de fin</th>
              <th>Coupe d'Europe</th>
              <th>Cotisation (€)</th>
              <th class="text-center">Actions</th>
            </tr>
          </ng-template>
          <ng-template pTemplate="body" let-season>
            <tr>
              <td><span class="font-bold text-red-700 text-base">{{ season.id }}</span></td>
              <td>{{ season.dateStart ? (season.dateStart | date:'dd/MM/yyyy') : '-' }}</td>
              <td>{{ season.dateEnd ? (season.dateEnd | date:'dd/MM/yyyy') : '-' }}</td>
              <td>
                <p-tag [severity]="season.european ? 'success' : 'secondary'" [value]="season.european ? 'Oui' : 'Non'"></p-tag>
              </td>
              <td>{{ season.montantCotisation != null ? (season.montantCotisation | currency:'EUR':'symbol':'1.2-2':'fr') : '-' }}</td>
              <td class="text-center">
                <div class="flex justify-content-center gap-2">
                  <button pButton icon="pi pi-pencil" class="p-button-rounded p-button-text p-button-sm p-button-warning" (click)="openEditDialog(season)"></button>
                  <button pButton icon="pi pi-trash" class="p-button-rounded p-button-text p-button-sm p-button-danger" (click)="confirmDelete(season)"></button>
                </div>
              </td>
            </tr>
          </ng-template>
        </p-table>
      </div>

      <!-- Season Dialog -->
      <p-dialog [(visible)]="dialogVisible" [header]="isEditMode ? 'Modifier la saison' : 'Nouvelle saison'" [modal]="true" [style]="{ width: '500px' }">
        <form [formGroup]="form" (ngSubmit)="saveSeason()" class="flex flex-column gap-3 mt-2">
          <div class="flex flex-column gap-2">
            <label for="id" class="font-semibold text-sm">Identifiant (généré automatiquement)</label>
            <input id="id" type="text" pInputText formControlName="id" [readonly]="true" placeholder="ex: 2024-2025" />
          </div>

          <div class="grid">
            <div class="col-12 md:col-6 flex flex-column gap-2">
              <label for="dateStart" class="font-semibold text-sm">Date de début *</label>
              <input id="dateStart" type="date" pInputText formControlName="dateStart" />
            </div>
            <div class="col-12 md:col-6 flex flex-column gap-2">
              <label for="dateEnd" class="font-semibold text-sm">Date de fin *</label>
              <input id="dateEnd" type="date" pInputText formControlName="dateEnd" />
            </div>
          </div>

          <div class="flex flex-column gap-2">
            <label for="montantCotisation" class="font-semibold text-sm">Montant de la cotisation (€)</label>
            <p-inputNumber
              inputId="montantCotisation"
              formControlName="montantCotisation"
              mode="currency"
              currency="EUR"
              locale="fr-FR"
              [min]="0"
              placeholder="ex: 15,00 €">
            </p-inputNumber>
          </div>

          <div class="flex align-items-center gap-2 mt-2">
            <p-checkbox formControlName="european" [binary]="true" inputId="european"></p-checkbox>
            <label for="european" class="text-sm font-medium">Coupe d'Europe</label>
          </div>

          <div class="flex justify-content-end gap-2 mt-4">
            <button pButton type="button" label="Annuler" icon="pi pi-times" [text]="true" (click)="dialogVisible = false"></button>
            <button pButton type="submit" label="Enregistrer" icon="pi pi-check" class="p-button-danger font-bold" [disabled]="form.invalid"></button>
          </div>
        </form>
      </p-dialog>
    </div>
  `
})
export class SeasonListComponent implements OnInit {
  private seasonService = inject(SeasonService);
  private messageService = inject(MessageService);
  private confirmationService = inject(ConfirmationService);
  private fb = inject(FormBuilder);

  seasons: Season[] = [];
  loading = false;
  dialogVisible = false;
  isEditMode = false;

  form: FormGroup = this.fb.group({
    id: [''],
    dateStart: ['', Validators.required],
    dateEnd: ['', Validators.required],
    european: [false],
    montantCotisation: [null]
  });

  ngOnInit(): void {
    this.loadSeasons();
    this.form.get('dateStart')?.valueChanges.subscribe(() => this.updateCalculatedId());
    this.form.get('dateEnd')?.valueChanges.subscribe(() => this.updateCalculatedId());
  }

  private updateCalculatedId(): void {
    if (this.isEditMode) return;
    const start = this.form.get('dateStart')?.value;
    const end = this.form.get('dateEnd')?.value;
    if (start && end) {
      const yearStart = start.split('-')[0];
      const yearEnd = end.split('-')[0];
      if (yearStart && yearEnd) {
        this.form.patchValue({ id: `${yearStart}-${yearEnd}` }, { emitEvent: false });
      }
    }
  }

  loadSeasons(): void {
    this.loading = true;
    this.seasonService.getAllSeasons().subscribe({
      next: (data) => {
        this.seasons = data;
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  openNewDialog(): void {
    this.isEditMode = false;
    this.form.reset({ european: false, montantCotisation: null, id: '' });
    this.dialogVisible = true;
  }

  openEditDialog(season: Season): void {
    this.isEditMode = true;
    this.form.patchValue({
      id: season.id,
      dateStart: season.dateStart,
      dateEnd: season.dateEnd,
      european: season.european,
      montantCotisation: season.montantCotisation
    });
    this.dialogVisible = true;
  }

  saveSeason(): void {
    if (this.form.invalid) return;
    const val = this.form.value as SeasonCreateUpdate;

    if (!this.isEditMode) {
      if (val.dateStart && val.dateEnd) {
        const yearStart = val.dateStart.split('-')[0];
        const yearEnd = val.dateEnd.split('-')[0];
        val.id = `${yearStart}-${yearEnd}`;
      }
      if (this.seasons.some(s => s.id === val.id)) {
        this.messageService.add({
          severity: 'error',
          summary: 'Erreur',
          detail: `Une saison avec l'identifiant ${val.id} existe déjà.`
        });
        return;
      }
      this.seasonService.createSeason(val).subscribe({
        next: () => {
          this.dialogVisible = false;
          this.messageService.add({ severity: 'success', summary: 'Succès', detail: 'Saison créée' });
          this.loadSeasons();
        },
        error: (err) => {
          this.messageService.add({
            severity: 'error',
            summary: 'Erreur',
            detail: err?.error?.detail || err?.error?.message || 'Erreur lors de la création de la saison'
          });
        }
      });
    } else {
      this.seasonService.updateSeason(val.id!, val).subscribe({
        next: () => {
          this.dialogVisible = false;
          this.messageService.add({ severity: 'success', summary: 'Succès', detail: 'Saison modifiée' });
          this.loadSeasons();
        },
        error: (err) => {
          this.messageService.add({
            severity: 'error',
            summary: 'Erreur',
            detail: err?.error?.detail || err?.error?.message || 'Erreur lors de la modification de la saison'
          });
        }
      });
    }
  }

  confirmDelete(season: Season): void {
    this.confirmationService.confirm({
      message: `Supprimer la saison ${season.id} ?`,
      acceptButtonStyleClass: 'p-button-danger',
      accept: () => {
        this.seasonService.deleteSeason(season.id).subscribe({
          next: () => {
            this.messageService.add({ severity: 'success', summary: 'Supprimé', detail: 'Saison supprimée' });
            this.loadSeasons();
          },
          error: (err) => {
            this.messageService.add({
              severity: 'error',
              summary: 'Erreur',
              detail: err?.error?.detail || err?.error?.message || 'Erreur lors de la suppression de la saison'
            });
          }
        });
      }
    });
  }
}
