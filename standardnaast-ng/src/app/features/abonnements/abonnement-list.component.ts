import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { TableModule, TableLazyLoadEvent } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { InputNumberModule } from 'primeng/inputnumber';
import { DropdownModule } from 'primeng/dropdown';
import { CheckboxModule } from 'primeng/checkbox';
import { DialogModule } from 'primeng/dialog';
import { TagModule } from 'primeng/tag';
import { ConfirmationService, MessageService } from 'primeng/api';
import { AbonnementService } from '../../core/services/abonnement.service';
import { SeasonService } from '../../core/services/season.service';
import { MemberService } from '../../core/services/member.service';
import { Abonnement, AbonnementCreateUpdate, AbonnementStatus, AbonnementPrice } from '../../core/models/abonnement.model';
import { Member } from '../../core/models/member.model';

@Component({
  selector: 'app-abonnement-list',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    TableModule,
    ButtonModule,
    InputTextModule,
    InputNumberModule,
    DropdownModule,
    CheckboxModule,
    DialogModule,
    TagModule
  ],
  template: `
    <div class="abonnements-page flex flex-column gap-2">
      <div class="flex flex-column sm:flex-row justify-content-between align-items-start sm:align-items-center gap-2 bg-white px-3 py-2 border-round-xl border-1 border-200 shadow-1">
        <div>
          <h1 class="text-xl font-bold text-900 m-0">Gestion des Abonnements</h1>
          <p class="text-500 text-xs m-0 mt-1">Suivi des commandes, réceptions et distributions d'abonnements</p>
        </div>
        <button pButton label="Nouvel Abonnement" icon="pi pi-plus" class="p-button-danger p-button-sm font-bold" (click)="openNewDialog()"></button>
      </div>

      <div class="surface-card p-2 sm:p-3 border-round-xl border-1 border-200 shadow-1">
        <p-table
          [value]="abonnements"
          [lazy]="true"
          (onLazyLoad)="loadAbonnements($event)"
          [paginator]="true"
          [rows]="pageSize"
          [totalRecords]="totalElements"
          [loading]="loading"
          responsiveLayout="stack"
          styleClass="p-datatable-sm p-datatable-striped">
          <ng-template pTemplate="header">
            <tr>
              <th>N° Membre</th>
              <th>Membre</th>
              <th>Bloc / Rang / Place</th>
              <th>Montant</th>
              <th>Payé</th>
              <th>Statut</th>
              <th class="text-center">Actions</th>
            </tr>
          </ng-template>
          <ng-template pTemplate="body" let-abo>
            <tr>
              <td><span class="font-bold text-red-700">{{ abo.memberNumber || abo.personMemberNumber || '-' }}</span></td>
              <td><span class="font-semibold">{{ abo.personFirstName }} {{ abo.personName }}</span></td>
              <td>{{ (abo.bloc || abo.abonnementPrice?.bloc || '-') }} / {{ abo.rang || '-' }} / {{ abo.place || '-' }}</td>
              <td>{{ (abo.acompte != null ? abo.acompte : abo.montantPaye) | currency:'EUR':'symbol':'1.2-2':'fr' }}</td>
              <td>
                <p-tag [severity]="abo.paye ? 'success' : 'danger'" [value]="abo.paye ? 'Oui' : 'Non'"></p-tag>
              </td>
              <td>
                <p-tag [severity]="getStatusSeverity(abo.abonnementStatus || abo.status)" [value]="abo.abonnementStatus || abo.status"></p-tag>
              </td>
              <td class="text-center">
                <div class="flex justify-content-center gap-2">
                  <button pButton icon="pi pi-pencil" class="p-button-rounded p-button-text p-button-sm p-button-warning" (click)="openEditDialog(abo)"></button>
                  <button pButton icon="pi pi-trash" class="p-button-rounded p-button-text p-button-sm p-button-danger" (click)="confirmDelete(abo)"></button>
                </div>
              </td>
            </tr>
          </ng-template>
        </p-table>
      </div>

      <!-- Abonnement Dialog -->
      <p-dialog [(visible)]="dialogVisible" [header]="isEditMode ? 'Modifier l\\'abonnement' : 'Nouvel abonnement'" [modal]="true" [style]="{ width: '600px' }">
        <form [formGroup]="form" (ngSubmit)="saveAbonnement()" class="flex flex-column gap-3 mt-2">
          <div class="grid">
            <div class="col-12 md:col-6 flex flex-column gap-2">
              <label class="font-semibold text-sm">Membre *</label>
              <p-dropdown [options]="members" optionLabel="name" optionValue="id" formControlName="personId" [filter]="true" placeholder="Choisir un membre"></p-dropdown>
            </div>
            <div class="col-12 md:col-6 flex flex-column gap-2">
              <label class="font-semibold text-sm">Saison *</label>
              <input type="text" pInputText formControlName="seasonId" />
            </div>
          </div>

          <div class="grid">
            <div class="col-12 md:col-6 flex flex-column gap-2">
              <label class="font-semibold text-sm">Tarif Abonnement *</label>
              <p-dropdown [options]="prices" optionLabel="id" optionValue="id" formControlName="abonnementPriceId" placeholder="Choisir un tarif"></p-dropdown>
            </div>
            <div class="col-12 md:col-6 flex flex-column gap-2">
              <label class="font-semibold text-sm">Statut</label>
              <p-dropdown [options]="statusOptions" formControlName="status"></p-dropdown>
            </div>
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

          <div class="grid">
            <div class="col-12 md:col-6 flex flex-column gap-2">
              <label class="font-semibold text-sm">Montant Payé (€)</label>
              <p-inputNumber formControlName="montantPaye" mode="currency" currency="EUR" locale="fr-FR"></p-inputNumber>
            </div>
            <div class="col-12 md:col-6 flex flex-column gap-2">
              <label class="font-semibold text-sm">Réduction (€)</label>
              <p-inputNumber formControlName="reduction" mode="currency" currency="EUR" locale="fr-FR"></p-inputNumber>
            </div>
          </div>

          <div class="flex align-items-center gap-2 mt-2">
            <p-checkbox formControlName="paye" [binary]="true" inputId="paye"></p-checkbox>
            <label for="paye" class="text-sm font-medium">Abonnement payé</label>
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
  private abonnementService = inject(AbonnementService);
  private seasonService = inject(SeasonService);
  private memberService = inject(MemberService);
  private messageService = inject(MessageService);
  private confirmationService = inject(ConfirmationService);
  private fb = inject(FormBuilder);

  abonnements: Abonnement[] = [];
  members: Member[] = [];
  prices: AbonnementPrice[] = [];
  totalElements = 0;
  pageSize = 20;
  loading = false;
  dialogVisible = false;
  isEditMode = false;
  selectedAbonnementId: number | null = null;

  statusOptions = [
    { label: 'Nouveau', value: AbonnementStatus.NEW },
    { label: 'Commandé', value: AbonnementStatus.ORDERED },
    { label: 'Reçu', value: AbonnementStatus.RECEIVED },
    { label: 'Distribué', value: AbonnementStatus.DISTRIBUTED }
  ];

  form: FormGroup = this.fb.group({
    personId: [null, Validators.required],
    seasonId: ['', Validators.required],
    abonnementPriceId: [null, Validators.required],
    rang: [''],
    place: [''],
    montantPaye: [0],
    reduction: [0],
    paye: [false],
    status: [AbonnementStatus.NEW]
  });

  ngOnInit(): void {
    this.memberService.getMembers(undefined, 0, 1000).subscribe({
      next: (res) => this.members = res.content
    });
  }

  loadAbonnements(event: TableLazyLoadEvent): void {
    this.loading = true;
    const page = event.first ? Math.floor(event.first / (event.rows || this.pageSize)) : 0;
    const size = event.rows || this.pageSize;
    const curSeason = this.seasonService.selectedSeason()?.id;

    if (curSeason) {
      this.abonnementService.getPricesBySeason(curSeason).subscribe({
        next: (pr) => this.prices = pr
      });
    }

    this.abonnementService.getAbonnements(curSeason, undefined, undefined, page, size).subscribe({
      next: (res) => {
        this.abonnements = res.content;
        this.totalElements = res.totalElements;
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  getStatusSeverity(status: AbonnementStatus): string {
    switch (status) {
      case AbonnementStatus.NEW: return 'info';
      case AbonnementStatus.ORDERED: return 'warning';
      case AbonnementStatus.RECEIVED: return 'primary';
      case AbonnementStatus.DISTRIBUTED: return 'success';
      default: return 'secondary';
    }
  }

  openNewDialog(): void {
    this.isEditMode = false;
    this.selectedAbonnementId = null;
    const curSeason = this.seasonService.selectedSeason()?.id || '';
    this.form.reset({
      seasonId: curSeason,
      paye: false,
      status: AbonnementStatus.NEW,
      montantPaye: 0,
      reduction: 0
    });
    this.dialogVisible = true;
  }

  openEditDialog(abo: Abonnement): void {
    this.isEditMode = true;
    this.selectedAbonnementId = abo.id;
    this.form.patchValue({
      personId: abo.memberId,
      seasonId: abo.seasonId,
      abonnementPriceId: abo.abonnementPrice?.id,
      rang: abo.rang,
      place: abo.place,
      montantPaye: abo.montantPaye,
      reduction: abo.reduction,
      paye: abo.paye,
      status: abo.status
    });
    this.dialogVisible = true;
  }

  saveAbonnement(): void {
    if (this.form.invalid) return;
    const val = this.form.value as AbonnementCreateUpdate;

    if (this.isEditMode && this.selectedAbonnementId) {
      this.abonnementService.updateAbonnement(this.selectedAbonnementId, val).subscribe({
        next: () => {
          this.dialogVisible = false;
          this.messageService.add({ severity: 'success', summary: 'Succès', detail: 'Abonnement modifié' });
          this.loadAbonnements({ first: 0, rows: this.pageSize });
        }
      });
    } else {
      this.abonnementService.createAbonnement(val).subscribe({
        next: () => {
          this.dialogVisible = false;
          this.messageService.add({ severity: 'success', summary: 'Succès', detail: 'Abonnement créé' });
          this.loadAbonnements({ first: 0, rows: this.pageSize });
        }
      });
    }
  }

  confirmDelete(abo: Abonnement): void {
    this.confirmationService.confirm({
      message: `Supprimer l'abonnement pour ${abo.personFirstName} ${abo.personName} ?`,
      acceptButtonStyleClass: 'p-button-danger',
      accept: () => {
        this.abonnementService.deleteAbonnement(abo.id).subscribe({
          next: () => {
            this.messageService.add({ severity: 'success', summary: 'Supprimé', detail: 'Abonnement supprimé' });
            this.loadAbonnements({ first: 0, rows: this.pageSize });
          }
        });
      }
    });
  }
}
