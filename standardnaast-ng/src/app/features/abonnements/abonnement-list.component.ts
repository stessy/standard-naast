import { Component, OnInit, ViewChild, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Table, TableModule, TableLazyLoadEvent } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { DropdownModule } from 'primeng/dropdown';
import { DialogModule } from 'primeng/dialog';
import { ConfirmationService, MessageService } from 'primeng/api';
import { AbonnementService } from '../../core/services/abonnement.service';
import { SeasonService } from '../../core/services/season.service';
import { MemberService } from '../../core/services/member.service';
import { Abonnement, AbonnementCreateUpdate } from '../../core/models/abonnement.model';
import { Season } from '../../core/models/season.model';
import { Member } from '../../core/models/member.model';

@Component({
  selector: 'app-abonnement-list',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    TableModule,
    ButtonModule,
    InputTextModule,
    DropdownModule,
    DialogModule
  ],
  template: `
    <div class="abonnements-page flex flex-column gap-2">
      <div class="flex flex-column sm:flex-row justify-content-between align-items-start sm:align-items-center gap-2 bg-white px-3 py-2 border-round-xl border-1 border-200 shadow-1">
        <div>
          <h1 class="text-xl font-bold text-900 m-0">Gestion des Abonnements</h1>
          <p class="text-500 text-xs m-0 mt-1">Gestion des abonnements des membres par saison</p>
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
          <button pButton label="Nouvel Abonnement" icon="pi pi-plus" class="p-button-danger p-button-sm font-bold" (click)="openNewDialog()"></button>
        </div>
      </div>

      <div class="surface-card p-2 sm:p-3 border-round-xl border-1 border-200 shadow-1">
        <p-table
          #table
          [value]="abonnements"
          [lazy]="true"
          (onLazyLoad)="loadAbonnements($event)"
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
              <th pSortableColumn="memberNumber">N° Membre <p-sortIcon field="memberNumber"></p-sortIcon></th>
              <th>Membre</th>
              <th>Saison</th>
              <th>Bloc</th>
              <th>Rang</th>
              <th>Place</th>
              <th class="text-center">Actions</th>
            </tr>
          </ng-template>
          <ng-template pTemplate="body" let-abo>
            <tr>
              <td><span class="font-bold text-red-700">{{ abo.memberNumber || abo.personMemberNumber || '-' }}</span></td>
              <td><span class="font-semibold">{{ abo.personFirstName }} {{ abo.personName }}</span></td>
              <td><span class="font-medium text-700">{{ abo.seasonId }}</span></td>
              <td>{{ abo.bloc || abo.abonnementPrice?.bloc || '-' }}</td>
              <td>{{ abo.rang || '-' }}</td>
              <td>{{ abo.place || '-' }}</td>
              <td class="text-center">
                <div class="flex justify-content-center gap-2">
                  <button pButton icon="pi pi-pencil" class="p-button-rounded p-button-text p-button-sm p-button-warning" (click)="openEditDialog(abo)"></button>
                  <button pButton icon="pi pi-trash" class="p-button-rounded p-button-text p-button-sm p-button-danger" (click)="confirmDelete(abo)"></button>
                </div>
              </td>
            </tr>
          </ng-template>
          <ng-template pTemplate="emptymessage">
            <tr>
              <td colspan="7" class="text-center p-3 text-500">Aucun abonnement trouvé pour cette sélection.</td>
            </tr>
          </ng-template>
        </p-table>
      </div>

      <!-- Abonnement Dialog -->
      <p-dialog [(visible)]="dialogVisible" [header]="isEditMode ? 'Modifier l\\'abonnement' : 'Nouvel abonnement'" [modal]="true" [style]="{ width: '500px' }">
        <form [formGroup]="form" (ngSubmit)="saveAbonnement()" class="flex flex-column gap-3 mt-2">
          <div class="grid">
            <div class="col-12 md:col-6 flex flex-column gap-2">
              <label class="font-semibold text-sm">Membre *</label>
              <p-dropdown
                [options]="members"
                optionLabel="displayName"
                optionValue="id"
                formControlName="personId"
                [filter]="true"
                filterBy="name,firstname,displayName"
                placeholder="Choisir un membre"
                (onChange)="onDialogMemberChange($event.value)"
                [style]="{ width: '100%' }">
              </p-dropdown>
            </div>
            <div class="col-12 md:col-6 flex flex-column gap-2">
              <label class="font-semibold text-sm">Saison *</label>
              <p-dropdown
                [options]="seasons"
                optionLabel="id"
                optionValue="id"
                formControlName="seasonId"
                placeholder="Choisir une saison">
              </p-dropdown>
            </div>
          </div>

          <div class="flex flex-column gap-2">
            <label class="font-semibold text-sm">Bloc *</label>
            <p-dropdown
              [options]="availableBlocs"
              formControlName="bloc"
              [filter]="true"
              placeholder="Choisir un bloc"
              [style]="{ width: '100%' }">
            </p-dropdown>
          </div>

          <div class="grid">
            <div class="col-12 md:col-6 flex flex-column gap-2">
              <label class="font-semibold text-sm">Rang</label>
              <input type="text" pInputText formControlName="rang" />
            </div>
            <div class="col-12 md:col-6 flex flex-column gap-2">
              <label class="font-semibold text-sm">Place</label>
              <input type="text" pInputText formControlName="place" />
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
export class AbonnementListComponent implements OnInit {
  @ViewChild('table') table?: Table;

  private abonnementService = inject(AbonnementService);
  private seasonService = inject(SeasonService);
  private memberService = inject(MemberService);
  private messageService = inject(MessageService);
  private confirmationService = inject(ConfirmationService);
  private fb = inject(FormBuilder);

  abonnements: Abonnement[] = [];
  members: (Member & { displayName?: string })[] = [];
  seasons: Season[] = [];
  availableBlocs: string[] = [];
  selectedSeasonId: string | null = null;
  totalElements = 0;
  pageSize = 20;
  loading = false;
  dialogVisible = false;
  isEditMode = false;
  selectedAbonnementId: number | null = null;

  form: FormGroup = this.fb.group({
    personId: [null, Validators.required],
    seasonId: ['', Validators.required],
    bloc: ['', Validators.required],
    rang: [''],
    place: ['']
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

    this.loadAvailableBlocs();
  }

  loadAvailableBlocs(): void {
    this.abonnementService.getAvailableBlocs().subscribe({
      next: (blocs) => (this.availableBlocs = blocs),
      error: () => (this.availableBlocs = [])
    });
  }

  onSeasonFilterChange(seasonId: string | null): void {
    this.selectedSeasonId = seasonId;
    if (this.table) {
      this.table.reset();
    } else {
      this.loadAbonnements({ first: 0, rows: this.pageSize });
    }
  }

  loadAbonnements(event: TableLazyLoadEvent): void {
    this.loading = true;
    const page = event.first ? Math.floor(event.first / (event.rows || this.pageSize)) : 0;
    const size = event.rows || this.pageSize;
    const seasonToFilter = this.selectedSeasonId || undefined;

    let sort = 'personne.memberNumber,asc';
    if (event.sortField) {
      const field = Array.isArray(event.sortField) ? event.sortField[0] : event.sortField;
      const order = event.sortOrder === -1 ? 'desc' : 'asc';
      const backendField = field === 'memberNumber' ? 'personne.memberNumber' : field;
      sort = `${backendField},${order}`;
    }

    this.abonnementService.getAbonnements(seasonToFilter, undefined, undefined, page, size, sort).subscribe({
      next: (res) => {
        this.abonnements = res.content;
        this.totalElements = res.totalElements;
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  onDialogMemberChange(memberId: number): void {
    if (!memberId || this.isEditMode) return;
    this.abonnementService.getLatestAbonnementByMember(memberId).subscribe({
      next: (latestAbo) => {
        if (latestAbo) {
          this.form.patchValue({
            bloc: latestAbo.bloc || latestAbo.abonnementPrice?.bloc || '',
            rang: latestAbo.rang || '',
            place: latestAbo.place || ''
          });
        }
      }
    });
  }

  openNewDialog(): void {
    this.isEditMode = false;
    this.selectedAbonnementId = null;
    const seasonId = this.selectedSeasonId || this.seasonService.selectedSeason()?.id || (this.seasons.length > 0 ? this.seasons[0].id : '');
    this.form.reset({
      personId: null,
      seasonId: seasonId,
      bloc: '',
      rang: '',
      place: ''
    });
    this.dialogVisible = true;
  }

  openEditDialog(abo: Abonnement): void {
    this.isEditMode = true;
    this.selectedAbonnementId = abo.id;
    const seasonId = abo.seasonId || this.selectedSeasonId || '';
    this.form.patchValue({
      personId: abo.memberId || abo.personId,
      seasonId: seasonId,
      bloc: abo.bloc || abo.abonnementPrice?.bloc || '',
      rang: abo.rang || '',
      place: abo.place || ''
    });
    this.dialogVisible = true;
  }

  saveAbonnement(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const val = this.form.value as AbonnementCreateUpdate;

    if (this.isEditMode && this.selectedAbonnementId) {
      this.abonnementService.updateAbonnement(this.selectedAbonnementId, val).subscribe({
        next: () => {
          this.dialogVisible = false;
          this.messageService.add({ severity: 'success', summary: 'Succès', detail: 'Abonnement modifié' });
          this.loadAbonnements({ first: 0, rows: this.pageSize });
        },
        error: () => {
          this.messageService.add({ severity: 'error', summary: 'Erreur', detail: "Impossible de modifier l'abonnement" });
        }
      });
    } else {
      this.abonnementService.createAbonnement(val).subscribe({
        next: () => {
          this.dialogVisible = false;
          this.messageService.add({ severity: 'success', summary: 'Succès', detail: 'Abonnement créé' });
          this.loadAbonnements({ first: 0, rows: this.pageSize });
        },
        error: () => {
          this.messageService.add({ severity: 'error', summary: 'Erreur', detail: "Impossible de créer l'abonnement" });
        }
      });
    }
  }

  confirmDelete(abo: Abonnement): void {
    this.confirmationService.confirm({
      message: `Supprimer l'abonnement pour ${abo.personFirstName || ''} ${abo.personName || ''} ?`,
      header: "Suppression de l'abonnement",
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Oui',
      rejectLabel: 'Non',
      acceptButtonStyleClass: 'p-button-danger',
      rejectButtonStyleClass: 'p-button-text',
      accept: () => {
        this.abonnementService.deleteAbonnement(abo.id).subscribe({
          next: () => {
            this.messageService.add({ severity: 'success', summary: 'Succès', detail: 'Abonnement supprimé' });
            this.loadAbonnements({ first: 0, rows: this.pageSize });
          },
          error: () => {
            this.messageService.add({ severity: 'error', summary: 'Erreur', detail: "Impossible de supprimer l'abonnement" });
          }
        });
      }
    });
  }
}
