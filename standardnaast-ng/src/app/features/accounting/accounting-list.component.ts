import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { TableModule, TableLazyLoadEvent } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { InputNumberModule } from 'primeng/inputnumber';
import { DropdownModule } from 'primeng/dropdown';
import { DialogModule } from 'primeng/dialog';
import { TagModule } from 'primeng/tag';
import { ConfirmationService, MessageService } from 'primeng/api';
import { AccountingService } from '../../core/services/accounting.service';
import { Accounting, AccountingCreateUpdate, AccountingType, AccountingSummary } from '../../core/models/accounting.model';

@Component({
  selector: 'app-accounting-list',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    TableModule,
    ButtonModule,
    InputTextModule,
    InputNumberModule,
    DropdownModule,
    DialogModule,
    TagModule
  ],
  template: `
    <div class="accounting-page flex flex-column gap-4">
      <div class="flex flex-column sm:flex-row justify-content-between align-items-start sm:align-items-center gap-3 bg-white p-4 border-round-xl border-1 border-200 shadow-1">
        <div>
          <h1 class="text-2xl font-bold text-900 m-0">Comptabilité & Trésorerie</h1>
          <p class="text-500 m-0 mt-1">Livre des recettes et dépenses de l'association</p>
        </div>
        <button pButton label="Nouvelle Écriture" icon="pi pi-plus" class="p-button-danger font-bold" (click)="openNewDialog()"></button>
      </div>

      <!-- KPI Summary Cards -->
      <div class="grid" *ngIf="summary">
        <div class="col-12 sm:col-4">
          <div class="surface-card p-4 border-round-xl border-1 border-200 shadow-1">
            <span class="text-500 font-medium text-green-600">Total Recettes</span>
            <div class="text-green-600 font-bold text-3xl mt-2">{{ summary.totalEntries | currency:'EUR':'symbol':'1.2-2':'fr' }}</div>
          </div>
        </div>
        <div class="col-12 sm:col-4">
          <div class="surface-card p-4 border-round-xl border-1 border-200 shadow-1">
            <span class="text-500 font-medium text-red-600">Total Dépenses</span>
            <div class="text-red-600 font-bold text-3xl mt-2">{{ summary.totalExits | currency:'EUR':'symbol':'1.2-2':'fr' }}</div>
          </div>
        </div>
        <div class="col-12 sm:col-4">
          <div class="surface-card p-4 border-round-xl border-1 border-200 shadow-1">
            <span class="text-500 font-medium">Solde Net</span>
            <div class="text-900 font-bold text-3xl mt-2" [class.text-green-600]="summary.balance >= 0" [class.text-red-600]="summary.balance < 0">
              {{ summary.balance | currency:'EUR':'symbol':'1.2-2':'fr' }}
            </div>
          </div>
        </div>
      </div>

      <div class="surface-card p-4 border-round-xl border-1 border-200 shadow-1">
        <p-table
          [value]="accountings"
          [lazy]="true"
          (onLazyLoad)="loadAccountings($event)"
          [paginator]="true"
          [rows]="pageSize"
          [totalRecords]="totalElements"
          [loading]="loading"
          responsiveLayout="stack"
          styleClass="p-datatable-striped">
          <ng-template pTemplate="header">
            <tr>
              <th>Date</th>
              <th>Description</th>
              <th>Type</th>
              <th>Montant</th>
              <th class="text-center">Actions</th>
            </tr>
          </ng-template>
          <ng-template pTemplate="body" let-acc>
            <tr>
              <td>{{ acc.date | date:'dd/MM/yyyy' }}</td>
              <td><span class="font-medium text-900">{{ acc.description }}</span></td>
              <td>
                <p-tag [severity]="acc.type === 'ENTRY' ? 'success' : 'danger'" [value]="acc.type === 'ENTRY' ? 'Recette' : 'Dépense'"></p-tag>
              </td>
              <td>
                <span class="font-bold" [class.text-green-600]="acc.type === 'ENTRY'" [class.text-red-600]="acc.type === 'EXIT'">
                  {{ acc.type === 'ENTRY' ? '+' : '-' }} {{ acc.amount | currency:'EUR':'symbol':'1.2-2':'fr' }}
                </span>
              </td>
              <td class="text-center">
                <div class="flex justify-content-center gap-2">
                  <button pButton icon="pi pi-pencil" class="p-button-rounded p-button-text p-button-warning" (click)="openEditDialog(acc)"></button>
                  <button pButton icon="pi pi-trash" class="p-button-rounded p-button-text p-button-danger" (click)="confirmDelete(acc)"></button>
                </div>
              </td>
            </tr>
          </ng-template>
          <ng-template pTemplate="emptymessage">
            <tr><td colspan="5" class="text-center p-4 text-500">Aucune écriture comptable trouvée.</td></tr>
          </ng-template>
        </p-table>
      </div>

      <!-- Accounting Dialog -->
      <p-dialog [(visible)]="dialogVisible" [header]="isEditMode ? 'Modifier l\\'écriture' : 'Nouvelle écriture'" [modal]="true" [style]="{ width: '480px' }">
        <form [formGroup]="form" (ngSubmit)="saveAccounting()" class="flex flex-column gap-3 mt-2">
          <div class="flex flex-column gap-2">
            <label class="font-semibold text-sm">Date *</label>
            <input type="date" pInputText formControlName="date" />
          </div>

          <div class="flex flex-column gap-2">
            <label class="font-semibold text-sm">Description *</label>
            <input type="text" pInputText formControlName="description" placeholder="Ex: Vente boissons..." />
          </div>

          <div class="grid">
            <div class="col-12 md:col-6 flex flex-column gap-2">
              <label class="font-semibold text-sm">Type *</label>
              <p-dropdown [options]="typeOptions" formControlName="type"></p-dropdown>
            </div>
            <div class="col-12 md:col-6 flex flex-column gap-2">
              <label class="font-semibold text-sm">Montant (€) *</label>
              <p-inputNumber formControlName="amount" mode="currency" currency="EUR" locale="fr-FR"></p-inputNumber>
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
export class AccountingListComponent implements OnInit {
  private accountingService = inject(AccountingService);
  private messageService = inject(MessageService);
  private confirmationService = inject(ConfirmationService);
  private fb = inject(FormBuilder);

  accountings: Accounting[] = [];
  summary: AccountingSummary | null = null;
  totalElements = 0;
  pageSize = 20;
  loading = false;
  dialogVisible = false;
  isEditMode = false;
  selectedAccountingId: number | null = null;

  typeOptions = [
    { label: 'Recette (+)', value: AccountingType.ENTRY },
    { label: 'Dépense (-)', value: AccountingType.EXIT }
  ];

  form: FormGroup = this.fb.group({
    date: ['', Validators.required],
    description: ['', Validators.required],
    type: [AccountingType.ENTRY, Validators.required],
    amount: [null, Validators.required]
  });

  ngOnInit(): void {
    this.loadSummary();
  }

  loadSummary(): void {
    this.accountingService.getSummary().subscribe({
      next: (data) => this.summary = data
    });
  }

  loadAccountings(event: TableLazyLoadEvent): void {
    this.loading = true;
    const page = event.first ? Math.floor(event.first / (event.rows || this.pageSize)) : 0;
    const size = event.rows || this.pageSize;

    this.accountingService.searchAccountings(undefined, undefined, undefined, undefined, page, size).subscribe({
      next: (res) => {
        this.accountings = res.content;
        this.totalElements = res.totalElements;
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  openNewDialog(): void {
    this.isEditMode = false;
    this.selectedAccountingId = null;
    this.form.reset({
      date: new Date().toISOString().substring(0, 10),
      type: AccountingType.ENTRY
    });
    this.dialogVisible = true;
  }

  openEditDialog(acc: Accounting): void {
    this.isEditMode = true;
    this.selectedAccountingId = acc.id;
    this.form.patchValue({
      date: acc.date,
      description: acc.description,
      type: acc.type,
      amount: acc.amount
    });
    this.dialogVisible = true;
  }

  saveAccounting(): void {
    if (this.form.invalid) return;
    const val = this.form.value as AccountingCreateUpdate;

    if (this.isEditMode && this.selectedAccountingId) {
      this.accountingService.updateAccounting(this.selectedAccountingId, val).subscribe({
        next: () => {
          this.dialogVisible = false;
          this.messageService.add({ severity: 'success', summary: 'Succès', detail: 'Écriture modifiée' });
          this.loadAccountings({ first: 0, rows: this.pageSize });
          this.loadSummary();
        }
      });
    } else {
      this.accountingService.createAccounting(val).subscribe({
        next: () => {
          this.dialogVisible = false;
          this.messageService.add({ severity: 'success', summary: 'Succès', detail: 'Écriture enregistrée' });
          this.loadAccountings({ first: 0, rows: this.pageSize });
          this.loadSummary();
        }
      });
    }
  }

  confirmDelete(acc: Accounting): void {
    this.confirmationService.confirm({
      message: `Supprimer l'écriture "${acc.description}" ?`,
      acceptButtonStyleClass: 'p-button-danger',
      accept: () => {
        this.accountingService.deleteAccounting(acc.id).subscribe({
          next: () => {
            this.messageService.add({ severity: 'success', summary: 'Supprimé', detail: 'Écriture supprimée' });
            this.loadAccountings({ first: 0, rows: this.pageSize });
            this.loadSummary();
          }
        });
      }
    });
  }
}
