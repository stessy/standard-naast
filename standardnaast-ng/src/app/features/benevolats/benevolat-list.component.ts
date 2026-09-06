import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { TableModule, TableLazyLoadEvent } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { InputNumberModule } from 'primeng/inputnumber';
import { DropdownModule } from 'primeng/dropdown';
import { DialogModule } from 'primeng/dialog';
import { ConfirmationService, MessageService } from 'primeng/api';
import { BenevolatService } from '../../core/services/benevolat.service';
import { MemberService } from '../../core/services/member.service';
import { Benevolat, BenevolatCreateUpdate } from '../../core/models/benevolat.model';
import { Member } from '../../core/models/member.model';

@Component({
  selector: 'app-benevolat-list',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    TableModule,
    ButtonModule,
    InputTextModule,
    InputNumberModule,
    DropdownModule,
    DialogModule
  ],
  template: `
    <div class="benevolats-page flex flex-column gap-4">
      <div class="flex flex-column sm:flex-row justify-content-between align-items-start sm:align-items-center gap-3 bg-white p-4 border-round-xl border-1 border-200 shadow-1">
        <div>
          <h1 class="text-2xl font-bold text-900 m-0">Gestion du Bénévolat</h1>
          <p class="text-500 m-0 mt-1">Prestations et dédommagements des bénévoles</p>
        </div>
        <button pButton label="Nouvelle Prestation" icon="pi pi-plus" class="p-button-danger font-bold" (click)="openNewDialog()"></button>
      </div>

      <div class="surface-card p-4 border-round-xl border-1 border-200 shadow-1">
        <p-table
          [value]="benevolats"
          [lazy]="true"
          (onLazyLoad)="loadBenevolats($event)"
          [paginator]="true"
          [rows]="pageSize"
          [totalRecords]="totalElements"
          [loading]="loading"
          responsiveLayout="stack"
          styleClass="p-datatable-striped">
          <ng-template pTemplate="header">
            <tr>
              <th>Date</th>
              <th>Bénévole</th>
              <th>Type de Prestation</th>
              <th>Montant (€)</th>
              <th class="text-center">Actions</th>
            </tr>
          </ng-template>
          <ng-template pTemplate="body" let-b>
            <tr>
              <td>{{ b.date | date:'dd/MM/yyyy' }}</td>
              <td><span class="font-bold text-900">{{ b.firstName }} {{ b.lastName }}</span></td>
              <td>{{ b.typeBenevolat }}</td>
              <td><span class="font-semibold text-green-700">{{ b.amount | currency:'EUR':'symbol':'1.2-2':'fr' }}</span></td>
              <td class="text-center">
                <div class="flex justify-content-center gap-2">
                  <button pButton icon="pi pi-pencil" class="p-button-rounded p-button-text p-button-warning" (click)="openEditDialog(b)"></button>
                  <button pButton icon="pi pi-trash" class="p-button-rounded p-button-text p-button-danger" (click)="confirmDelete(b)"></button>
                </div>
              </td>
            </tr>
          </ng-template>
          <ng-template pTemplate="emptymessage">
            <tr><td colspan="5" class="text-center p-4 text-500">Aucune prestation de bénévolat enregistrée.</td></tr>
          </ng-template>
        </p-table>
      </div>

      <!-- Benevolat Dialog -->
      <p-dialog [(visible)]="dialogVisible" [header]="isEditMode ? 'Modifier la prestation' : 'Nouvelle prestation'" [modal]="true" [style]="{ width: '480px' }">
        <form [formGroup]="form" (ngSubmit)="saveBenevolat()" class="flex flex-column gap-3 mt-2">
          <div class="flex flex-column gap-2">
            <label class="font-semibold text-sm">Bénévole / Membre *</label>
            <p-dropdown [options]="members" optionLabel="name" optionValue="id" formControlName="personId" [filter]="true" placeholder="Choisir une personne"></p-dropdown>
          </div>

          <div class="grid">
            <div class="col-12 md:col-6 flex flex-column gap-2">
              <label class="font-semibold text-sm">Date *</label>
              <input type="date" pInputText formControlName="date" />
            </div>
            <div class="col-12 md:col-6 flex flex-column gap-2">
              <label class="font-semibold text-sm">Montant (€) *</label>
              <p-inputNumber formControlName="amount" mode="currency" currency="EUR" locale="fr-FR"></p-inputNumber>
            </div>
          </div>

          <div class="flex flex-column gap-2">
            <label class="font-semibold text-sm">Type de prestation *</label>
            <input type="text" pInputText formControlName="typeBenevolat" placeholder="Ex: Barman, montage chapiteau..." />
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
export class BenevolatListComponent implements OnInit {
  private benevolatService = inject(BenevolatService);
  private memberService = inject(MemberService);
  private messageService = inject(MessageService);
  private confirmationService = inject(ConfirmationService);
  private fb = inject(FormBuilder);

  benevolats: Benevolat[] = [];
  members: Member[] = [];
  totalElements = 0;
  pageSize = 20;
  loading = false;
  dialogVisible = false;
  isEditMode = false;
  selectedBenevolatId: number | null = null;

  form: FormGroup = this.fb.group({
    personId: [null, Validators.required],
    date: ['', Validators.required],
    typeBenevolat: ['', Validators.required],
    amount: [null, Validators.required]
  });

  ngOnInit(): void {
    this.memberService.getMembers(undefined, 0, 1000).subscribe({
      next: (res) => this.members = res.content
    });
  }

  loadBenevolats(event: TableLazyLoadEvent): void {
    this.loading = true;
    const page = event.first ? Math.floor(event.first / (event.rows || this.pageSize)) : 0;
    const size = event.rows || this.pageSize;

    this.benevolatService.searchBenevolats(undefined, undefined, undefined, page, size).subscribe({
      next: (res) => {
        this.benevolats = res.content;
        this.totalElements = res.totalElements;
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  openNewDialog(): void {
    this.isEditMode = false;
    this.selectedBenevolatId = null;
    this.form.reset({
      date: new Date().toISOString().substring(0, 10)
    });
    this.dialogVisible = true;
  }

  openEditDialog(b: Benevolat): void {
    this.isEditMode = true;
    this.selectedBenevolatId = b.id;
    this.form.patchValue({
      personId: b.personId,
      date: b.date,
      typeBenevolat: b.typeBenevolat,
      amount: b.amount
    });
    this.dialogVisible = true;
  }

  saveBenevolat(): void {
    if (this.form.invalid) return;
    const val = this.form.value as BenevolatCreateUpdate;

    if (this.isEditMode && this.selectedBenevolatId) {
      this.benevolatService.updateBenevolat(this.selectedBenevolatId, val).subscribe({
        next: () => {
          this.dialogVisible = false;
          this.messageService.add({ severity: 'success', summary: 'Succès', detail: 'Prestation modifiée' });
          this.loadBenevolats({ first: 0, rows: this.pageSize });
        }
      });
    } else {
      this.benevolatService.createBenevolat(val).subscribe({
        next: () => {
          this.dialogVisible = false;
          this.messageService.add({ severity: 'success', summary: 'Succès', detail: 'Prestation enregistrée' });
          this.loadBenevolats({ first: 0, rows: this.pageSize });
        }
      });
    }
  }

  confirmDelete(b: Benevolat): void {
    this.confirmationService.confirm({
      message: `Supprimer la prestation de ${b.firstName} ${b.lastName} ?`,
      acceptButtonStyleClass: 'p-button-danger',
      accept: () => {
        this.benevolatService.deleteBenevolat(b.id).subscribe({
          next: () => {
            this.messageService.add({ severity: 'success', summary: 'Supprimé', detail: 'Prestation supprimée' });
            this.loadBenevolats({ first: 0, rows: this.pageSize });
          }
        });
      }
    });
  }
}
