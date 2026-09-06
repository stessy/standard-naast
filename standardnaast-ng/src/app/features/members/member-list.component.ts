import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { TableModule, TableLazyLoadEvent } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { DialogModule } from 'primeng/dialog';
import { CheckboxModule } from 'primeng/checkbox';
import { TagModule } from 'primeng/tag';
import { TooltipModule } from 'primeng/tooltip';
import { ConfirmationService, MessageService } from 'primeng/api';
import { MemberService } from '../../core/services/member.service';
import { Member, MemberCreateUpdate } from '../../core/models/member.model';

@Component({
  selector: 'app-member-list',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule,
    TableModule,
    ButtonModule,
    InputTextModule,
    DialogModule,
    CheckboxModule,
    TagModule,
    TooltipModule
  ],
  template: `
    <div class="members-page flex flex-column gap-4">
      <!-- Header Bar -->
      <div class="flex flex-column sm:flex-row justify-content-between align-items-start sm:align-items-center gap-3 bg-white p-4 border-round-xl border-1 border-200 shadow-1">
        <div>
          <h1 class="text-2xl font-bold text-900 m-0">Gestion des Membres</h1>
          <p class="text-500 m-0 mt-1">Consultez, recherchez et gérez les supporters du club</p>
        </div>
        <button
          pButton
          label="Nouveau Membre"
          icon="pi pi-user-plus"
          class="p-button-danger font-bold"
          (click)="openNewMemberDialog()">
        </button>
      </div>

      <!-- Main Table Card -->
      <div class="surface-card p-4 border-round-xl border-1 border-200 shadow-1">
        <!-- Search input -->
        <div class="flex justify-content-between align-items-center mb-3">
          <span class="p-input-icon-left w-full sm:w-20rem">
            <i class="pi pi-search"></i>
            <input
              pInputText
              type="text"
              placeholder="Rechercher par nom, prénom..."
              class="w-full"
              [(ngModel)]="searchTerm"
              (input)="onSearch()" />
          </span>
        </div>

        <!-- PrimeNG Table -->
        <p-table
          [value]="members"
          [lazy]="true"
          (onLazyLoad)="loadMembers($event)"
          [paginator]="true"
          [rows]="pageSize"
          [totalRecords]="totalElements"
          [loading]="loading"
          [rowsPerPageOptions]="[10, 20, 50]"
          styleClass="p-datatable-sm p-datatable-striped"
          responsiveLayout="stack">
          <ng-template pTemplate="header">
            <tr>
              <th pSortableColumn="memberNumber" style="width: 10%">N° <p-sortIcon field="memberNumber"></p-sortIcon></th>
              <th pSortableColumn="name" style="width: 25%">Nom <p-sortIcon field="name"></p-sortIcon></th>
              <th pSortableColumn="firstname" style="width: 25%">Prénom <p-sortIcon field="firstname"></p-sortIcon></th>
              <th style="width: 15%">Ville</th>
              <th style="width: 10%">Statut</th>
              <th style="width: 15%" class="text-center">Actions</th>
            </tr>
          </ng-template>
          <ng-template pTemplate="body" let-member>
            <tr>
              <td><span class="p-column-title">N° :</span><span class="font-bold text-red-700">{{ member.memberNumber || '-' }}</span></td>
              <td><span class="p-column-title">Nom :</span><span class="font-semibold">{{ member.name }}</span></td>
              <td><span class="p-column-title">Prénom :</span>{{ member.firstname }}</td>
              <td><span class="p-column-title">Ville :</span>{{ member.city || '-' }}</td>
              <td>
                <span class="p-column-title">Statut :</span>
                @if (member.redCard) {
                  <p-tag severity="danger" value="Carte Rouge"></p-tag>
                } @else if (member.student) {
                  <p-tag severity="info" value="Étudiant"></p-tag>
                } @else {
                  <p-tag severity="success" value="Actif"></p-tag>
                }
              </td>
              <td class="text-center">
                <div class="flex justify-content-center gap-1">
                  <button
                    pButton
                    icon="pi pi-eye"
                    class="p-button-rounded p-button-text p-button-sm p-button-info"
                    [routerLink]="['/members', member.id]"
                    pTooltip="Voir la fiche">
                  </button>
                  <button
                    pButton
                    icon="pi pi-pencil"
                    class="p-button-rounded p-button-text p-button-sm p-button-warning"
                    (click)="openEditMemberDialog(member)"
                    pTooltip="Modifier">
                  </button>
                  <button
                    pButton
                    icon="pi pi-trash"
                    class="p-button-rounded p-button-text p-button-sm p-button-danger"
                    (click)="confirmDeleteMember(member)"
                    pTooltip="Supprimer">
                  </button>
                </div>
              </td>
            </tr>
          </ng-template>
          <ng-template pTemplate="emptymessage">
            <tr>
              <td colspan="6" class="text-center p-4 text-500">Aucun membre trouvé.</td>
            </tr>
          </ng-template>
        </p-table>
      </div>

      <!-- Member Create / Edit Dialog -->
      <p-dialog
        [(visible)]="memberDialog"
        [header]="isEditMode ? 'Modifier le membre' : 'Nouveau membre'"
        [modal]="true"
        [style]="{ width: '600px' }"
        styleClass="p-fluid">
        <form [formGroup]="memberForm" (ngSubmit)="saveMember()" class="flex flex-column gap-3 mt-2">
          <div class="grid">
            <div class="col-12 md:col-6 flex flex-column gap-2">
              <label for="name" class="font-semibold text-sm">Nom *</label>
              <input id="name" type="text" pInputText formControlName="name" />
            </div>
            <div class="col-12 md:col-6 flex flex-column gap-2">
              <label for="firstname" class="font-semibold text-sm">Prénom *</label>
              <input id="firstname" type="text" pInputText formControlName="firstname" />
            </div>
          </div>

          <div class="grid">
            <div class="col-12 md:col-6 flex flex-column gap-2">
              <label for="email" class="font-semibold text-sm">Email</label>
              <input id="email" type="email" pInputText formControlName="email" />
            </div>
            <div class="col-12 md:col-6 flex flex-column gap-2">
              <label for="mobilePhone" class="font-semibold text-sm">Téléphone GSM</label>
              <input id="mobilePhone" type="text" pInputText formControlName="mobilePhone" />
            </div>
          </div>

          <div class="flex flex-column gap-2">
            <label for="address" class="font-semibold text-sm">Adresse</label>
            <input id="address" type="text" pInputText formControlName="address" />
          </div>

          <div class="grid">
            <div class="col-12 md:col-4 flex flex-column gap-2">
              <label for="postalCode" class="font-semibold text-sm">Code Postal</label>
              <input id="postalCode" type="text" pInputText formControlName="postalCode" />
            </div>
            <div class="col-12 md:col-8 flex flex-column gap-2">
              <label for="city" class="font-semibold text-sm">Ville</label>
              <input id="city" type="text" pInputText formControlName="city" />
            </div>
          </div>

          <div class="grid">
            <div class="col-12 md:col-6 flex flex-column gap-2">
              <label for="birthdate" class="font-semibold text-sm">Date de naissance</label>
              <input id="birthdate" type="date" pInputText formControlName="birthdate" />
            </div>
            <div class="col-12 md:col-6 flex flex-column gap-2">
              <label for="memberNumber" class="font-semibold text-sm">Numéro de Membre</label>
              <input id="memberNumber" type="number" pInputText formControlName="memberNumber" />
            </div>
          </div>

          <div class="flex gap-4 mt-2">
            <div class="flex align-items-center gap-2">
              <p-checkbox formControlName="student" [binary]="true" inputId="student"></p-checkbox>
              <label for="student" class="text-sm font-medium">Étudiant</label>
            </div>
            <div class="flex align-items-center gap-2">
              <p-checkbox formControlName="redCard" [binary]="true" inputId="redCard"></p-checkbox>
              <label for="redCard" class="text-sm font-medium text-red-600">Carte Rouge</label>
            </div>
          </div>

          <div class="flex justify-content-end gap-2 mt-4">
            <button pButton type="button" label="Annuler" icon="pi pi-times" [text]="true" (click)="memberDialog = false"></button>
            <button pButton type="submit" label="Enregistrer" icon="pi pi-check" class="p-button-danger font-bold" [loading]="saving"></button>
          </div>
        </form>
      </p-dialog>
    </div>
  `,
  styles: [`
    :host ::ng-deep .p-datatable.p-datatable-sm .p-datatable-tbody > tr > td {
      padding: 0.35rem 0.65rem;
      vertical-align: middle;
    }
    :host ::ng-deep .p-datatable.p-datatable-sm .p-datatable-thead > tr > th {
      padding: 0.5rem 0.65rem;
    }
  `]
})
export class MemberListComponent implements OnInit {
  private memberService = inject(MemberService);
  private messageService = inject(MessageService);
  private confirmationService = inject(ConfirmationService);
  private fb = inject(FormBuilder);

  members: Member[] = [];
  totalElements = 0;
  pageSize = 20;
  currentPage = 0;
  currentSort = 'name,asc';
  searchTerm = '';
  loading = false;
  saving = false;

  memberDialog = false;
  isEditMode = false;
  selectedMemberId: number | null = null;

  memberForm: FormGroup = this.fb.group({
    name: ['', Validators.required],
    firstname: ['', Validators.required],
    email: [''],
    mobilePhone: [''],
    phone: [''],
    address: [''],
    postalCode: [''],
    city: [''],
    birthdate: [''],
    memberNumber: [null],
    student: [false],
    redCard: [false]
  });

  ngOnInit(): void {}

  loadMembers(event: TableLazyLoadEvent): void {
    this.loading = true;
    const page = event.first ? Math.floor(event.first / (event.rows || this.pageSize)) : 0;
    const size = event.rows || this.pageSize;
    let sort = this.currentSort;

    if (event.sortField) {
      const field = Array.isArray(event.sortField) ? event.sortField[0] : event.sortField;
      const order = event.sortOrder === 1 ? 'asc' : 'desc';
      sort = `${field},${order}`;
    }

    this.currentPage = page;
    this.pageSize = size;
    this.currentSort = sort;

    this.memberService.getMembers(this.searchTerm, page, size, sort).subscribe({
      next: (response) => {
        this.members = response.content;
        this.totalElements = response.totalElements;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  onSearch(): void {
    this.loadMembers({ first: 0, rows: this.pageSize });
  }

  openNewMemberDialog(): void {
    this.isEditMode = false;
    this.selectedMemberId = null;
    this.memberForm.reset({
      student: false,
      redCard: false
    });

    this.memberService.getNextMemberNumber().subscribe({
      next: (nextNum) => {
        this.memberForm.patchValue({ memberNumber: nextNum });
      }
    });

    this.memberDialog = true;
  }

  openEditMemberDialog(member: Member): void {
    this.isEditMode = true;
    this.selectedMemberId = member.id;
    this.memberForm.patchValue({
      name: member.name,
      firstname: member.firstname,
      email: member.email,
      mobilePhone: member.mobilePhone,
      phone: member.phone,
      address: member.address,
      postalCode: member.postalCode,
      city: member.city,
      birthdate: member.birthdate,
      memberNumber: member.memberNumber,
      student: member.student,
      redCard: member.redCard
    });
    this.memberDialog = true;
  }

  saveMember(): void {
    if (this.memberForm.invalid) {
      this.memberForm.markAllAsTouched();
      return;
    }

    this.saving = true;
    const formValue = this.memberForm.value as MemberCreateUpdate;

    if (this.isEditMode && this.selectedMemberId) {
      this.memberService.updateMember(this.selectedMemberId, formValue).subscribe({
        next: () => {
          this.saving = false;
          this.memberDialog = false;
          this.messageService.add({ severity: 'success', summary: 'Succès', detail: 'Membre modifié avec succès' });
          this.loadMembers({ first: this.currentPage * this.pageSize, rows: this.pageSize });
        },
        error: () => {
          this.saving = false;
        }
      });
    } else {
      this.memberService.createMember(formValue).subscribe({
        next: () => {
          this.saving = false;
          this.memberDialog = false;
          this.messageService.add({ severity: 'success', summary: 'Succès', detail: 'Nouveau membre créé avec succès' });
          this.loadMembers({ first: 0, rows: this.pageSize });
        },
        error: () => {
          this.saving = false;
        }
      });
    }
  }

  confirmDeleteMember(member: Member): void {
    this.confirmationService.confirm({
      message: `Êtes-vous sûr de vouloir supprimer le membre ${member.firstname} ${member.name} ?`,
      header: 'Confirmation de suppression',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Supprimer',
      rejectLabel: 'Annuler',
      acceptButtonStyleClass: 'p-button-danger',
      accept: () => {
        this.memberService.deleteMember(member.id).subscribe({
          next: () => {
            this.messageService.add({ severity: 'success', summary: 'Supprimé', detail: 'Le membre a été supprimé' });
            this.loadMembers({ first: this.currentPage * this.pageSize, rows: this.pageSize });
          }
        });
      }
    });
  }
}
