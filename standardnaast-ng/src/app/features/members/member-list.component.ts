import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { TableModule, TableLazyLoadEvent, TableRowSelectEvent } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { DialogModule } from 'primeng/dialog';
import { CheckboxModule } from 'primeng/checkbox';
import { TagModule } from 'primeng/tag';
import { TooltipModule } from 'primeng/tooltip';
import { TabViewModule } from 'primeng/tabview';
import { ConfirmationService, MessageService } from 'primeng/api';
import { MemberService } from '../../core/services/member.service';
import { CotisationService } from '../../core/services/cotisation.service';
import { AbonnementService } from '../../core/services/abonnement.service';
import { TravelService } from '../../core/services/travel.service';
import { BenevolatService } from '../../core/services/benevolat.service';
import { Member, MemberCreateUpdate } from '../../core/models/member.model';
import { PersonCotisation } from '../../core/models/cotisation.model';
import { Abonnement } from '../../core/models/abonnement.model';
import { PersonTravel } from '../../core/models/travel.model';
import { Benevolat } from '../../core/models/benevolat.model';

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
    TooltipModule,
    TabViewModule
  ],
  template: `
    <div class="members-page flex flex-column gap-2">
      <!-- Header Bar with Filter below Title -->
      <div class="flex flex-column sm:flex-row justify-content-between align-items-start sm:align-items-center gap-2 bg-white px-3 py-2 border-round-xl border-1 border-200 shadow-1">
        <div class="flex flex-column gap-2 w-full sm:w-auto">
          <div class="flex align-items-center gap-2">
            <h1 class="text-xl font-bold text-900 m-0">Gestion des Membres</h1>
            <span class="text-500 text-xs">({{ totalElements }} membres)</span>
          </div>
          <span class="p-input-icon-left w-full sm:w-20rem">
            <i class="pi pi-search"></i>
            <input
              pInputText
              type="text"
              placeholder="Rechercher par nom, prénom..."
              class="p-inputtext-sm w-full"
              [(ngModel)]="searchTerm"
              (input)="onSearch()" />
          </span>
        </div>
        <button
          pButton
          label="Nouveau Membre"
          icon="pi pi-user-plus"
          class="p-button-danger p-button-sm font-bold white-space-nowrap align-self-start sm:align-self-center"
          (click)="openNewMemberDialog()">
        </button>
      </div>

      <!-- Main Table Card -->
      <div class="surface-card p-2 sm:p-3 border-round-xl border-1 border-200 shadow-1">
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
          sortField="memberNumber"
          [sortOrder]="1"
          [defaultSortOrder]="1"
          selectionMode="single"
          [(selection)]="selectedMember"
          (onRowSelect)="onRowSelect($event)"
          dataKey="id"
          [rowHover]="true"
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
            <tr [pSelectableRow]="member" class="cursor-pointer" (click)="selectMember(member)">
              <td><span class="font-bold text-red-700">{{ member.memberNumber || '-' }}</span></td>
              <td><span class="font-semibold">{{ member.name }}</span></td>
              <td>{{ member.firstname }}</td>
              <td>{{ member.city || '-' }}</td>
              <td>
                @if (member.redCard) {
                  <p-tag severity="danger" value="Carte Rouge"></p-tag>
                } @else {
                  <p-tag severity="success" value="Actif"></p-tag>
                }
              </td>
              <td class="text-center">
                <div class="flex justify-content-center gap-1" (click)="$event.stopPropagation()">
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
              <td colspan="6" class="text-center p-3 text-500">Aucun membre trouvé.</td>
            </tr>
          </ng-template>
        </p-table>
      </div>

      <!-- Member Details Card (Fiche complète du client au bas du tableau) -->
      @if (selectedMember) {
        <div class="surface-card p-3 sm:p-4 border-round-xl border-1 border-200 shadow-1">
          <div class="flex flex-column sm:flex-row justify-content-between align-items-start sm:align-items-center gap-2 mb-3 pb-2 border-bottom-1 border-200">
            <div class="flex align-items-center gap-2 flex-wrap">
              <i class="pi pi-id-card text-red-700 text-xl"></i>
              <h2 class="text-base sm:text-lg font-bold text-900 m-0">
                Fiche Complète : {{ selectedMember.firstname }} {{ selectedMember.name }}
              </h2>
              @if (selectedMember.memberNumber) {
                <span class="bg-red-100 text-red-800 font-bold px-2 py-0 border-round text-xs">
                  N° {{ selectedMember.memberNumber }}
                </span>
              }
              @if (selectedMember.redCard) {
                <p-tag severity="danger" value="Carte Rouge"></p-tag>
              } @else {
                <p-tag severity="success" value="Membre actif"></p-tag>
              }
            </div>
            <div class="flex gap-2">
              <button
                pButton
                type="button"
                label="Modifier"
                icon="pi pi-pencil"
                class="p-button-outlined p-button-sm p-button-warning font-semibold"
                (click)="openEditMemberDialog(selectedMember)">
              </button>
            </div>
          </div>

          <!-- TabView for Member Details -->
          <p-tabView>
            <!-- Identity Tab -->
            <p-tabPanel header="Identité" leftIcon="pi pi-user">
              <div class="grid text-sm p-2">
                <div class="col-12 sm:col-6 lg:col-3 flex flex-column gap-1">
                  <span class="text-500 text-xs font-semibold uppercase">Nom & Prénom</span>
                  <span class="text-900 font-medium">{{ selectedMember.firstname }} {{ selectedMember.name }}</span>
                </div>
                <div class="col-12 sm:col-6 lg:col-3 flex flex-column gap-1">
                  <span class="text-500 text-xs font-semibold uppercase">Date de naissance</span>
                  <span class="text-900 font-medium">{{ selectedMember.birthdate ? (selectedMember.birthdate | date:'dd/MM/yyyy') : '-' }}</span>
                </div>
                <div class="col-12 sm:col-6 lg:col-3 flex flex-column gap-1">
                  <span class="text-500 text-xs font-semibold uppercase">Téléphone GSM</span>
                  <span class="text-900 font-medium">{{ selectedMember.mobilePhone || '-' }}</span>
                </div>
                <div class="col-12 sm:col-6 lg:col-3 flex flex-column gap-1">
                  <span class="text-500 text-xs font-semibold uppercase">Email</span>
                  <span class="text-900 font-medium">{{ selectedMember.email || '-' }}</span>
                </div>
                <div class="col-12 sm:col-6 lg:col-3 flex flex-column gap-1">
                  <span class="text-500 text-xs font-semibold uppercase">Adresse</span>
                  <span class="text-900 font-medium">{{ selectedMember.address || '-' }}</span>
                </div>
                <div class="col-12 sm:col-6 lg:col-3 flex flex-column gap-1">
                  <span class="text-500 text-xs font-semibold uppercase">Code Postal & Ville</span>
                  <span class="text-900 font-medium">{{ selectedMember.postalCode || '' }} {{ selectedMember.city || '-' }}</span>
                </div>
                <div class="col-12 sm:col-6 lg:col-3 flex flex-column gap-1">
                  <span class="text-500 text-xs font-semibold uppercase">Numéro de Membre</span>
                  <span class="text-900 font-medium">{{ selectedMember.memberNumber || '-' }}</span>
                </div>
              </div>
            </p-tabPanel>

            <!-- Cotisations Tab -->
            <p-tabPanel header="Cotisations" leftIcon="pi pi-credit-card">
              <p-table [value]="selectedMemberCotisations" responsiveLayout="stack" styleClass="p-datatable-sm">
                <ng-template pTemplate="header">
                  <tr>
                    <th>Saison</th>
                    <th>Date de paiement</th>
                    <th>Carte envoyée</th>
                  </tr>
                </ng-template>
                <ng-template pTemplate="body" let-cot>
                  <tr>
                    <td><span class="font-bold">{{ cot.seasonId }}</span></td>
                    <td>{{ cot.datePaiement ? (cot.datePaiement | date:'dd/MM/yyyy') : 'Non payé' }}</td>
                    <td>
                      <p-tag [severity]="cot.carteMembreEnvoyee ? 'success' : 'warning'" [value]="cot.carteMembreEnvoyee ? 'Oui' : 'Non'"></p-tag>
                    </td>
                  </tr>
                </ng-template>
                <ng-template pTemplate="emptymessage">
                  <tr><td colspan="3" class="text-center p-3 text-500">Aucune cotisation enregistrée.</td></tr>
                </ng-template>
              </p-table>
            </p-tabPanel>

            <!-- Abonnements Tab -->
            <p-tabPanel header="Abonnements" leftIcon="pi pi-ticket">
              <p-table [value]="selectedMemberAbonnements" responsiveLayout="stack" styleClass="p-datatable-sm">
                <ng-template pTemplate="header">
                  <tr>
                    <th>Saison</th>
                    <th>Bloc</th>
                    <th>Rang</th>
                    <th>Place</th>
                    <th>Montant Payé</th>
                    <th>Statut</th>
                  </tr>
                </ng-template>
                <ng-template pTemplate="body" let-abo>
                  <tr>
                    <td><span class="font-bold">{{ abo.seasonId }}</span></td>
                    <td>{{ abo.bloc || '-' }}</td>
                    <td>{{ abo.rang || '-' }}</td>
                    <td>{{ abo.place || '-' }}</td>
                    <td>{{ (abo.acompte != null ? abo.acompte : abo.montantPaye) | currency:'EUR':'symbol':'1.2-2':'fr' }}</td>
                    <td>
                      <p-tag [value]="abo.abonnementStatus || abo.status"></p-tag>
                    </td>
                  </tr>
                </ng-template>
                <ng-template pTemplate="emptymessage">
                  <tr><td colspan="6" class="text-center p-3 text-500">Aucun abonnement enregistré.</td></tr>
                </ng-template>
              </p-table>
            </p-tabPanel>

            <!-- Travels Tab -->
            <p-tabPanel header="Déplacements" leftIcon="pi pi-car">
              <p-table [value]="selectedMemberTravels" responsiveLayout="stack" styleClass="p-datatable-sm">
                <ng-template pTemplate="header">
                  <tr>
                    <th>Match / Adversaire</th>
                    <th>Date</th>
                    <th>Montant payé</th>
                  </tr>
                </ng-template>
                <ng-template pTemplate="body" let-tr>
                  <tr>
                    <td><span class="font-semibold">{{ tr.opponentName }}</span></td>
                    <td>{{ tr.dateMatch | date:'dd/MM/yyyy' }}</td>
                    <td>{{ tr.amountPaid | currency:'EUR':'symbol':'1.2-2':'fr' }}</td>
                  </tr>
                </ng-template>
                <ng-template pTemplate="emptymessage">
                  <tr><td colspan="3" class="text-center p-3 text-500">Aucun déplacement enregistré.</td></tr>
                </ng-template>
              </p-table>
            </p-tabPanel>

            <!-- Benevolat Tab -->
            <p-tabPanel header="Bénévolat" leftIcon="pi pi-heart">
              <p-table [value]="selectedMemberBenevolats" responsiveLayout="stack" styleClass="p-datatable-sm">
                <ng-template pTemplate="header">
                  <tr>
                    <th>Date</th>
                    <th>Prestation</th>
                    <th>Montant</th>
                  </tr>
                </ng-template>
                <ng-template pTemplate="body" let-b>
                  <tr>
                    <td>{{ b.date | date:'dd/MM/yyyy' }}</td>
                    <td>{{ b.typeBenevolat }}</td>
                    <td>{{ b.amount | currency:'EUR':'symbol':'1.2-2':'fr' }}</td>
                  </tr>
                </ng-template>
                <ng-template pTemplate="emptymessage">
                  <tr><td colspan="3" class="text-center p-3 text-500">Aucune prestation de bénévolat.</td></tr>
                </ng-template>
              </p-table>
            </p-tabPanel>
          </p-tabView>
        </div>
      } @else {
        <div class="surface-card p-4 border-round-xl border-1 border-200 shadow-1 text-center text-500 text-sm">
          <i class="pi pi-info-circle mr-2"></i>
          Sélectionnez un membre dans le tableau pour afficher sa fiche complète.
        </div>
      }

      <!-- Member Create / Edit Dialog (Popup) -->
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
            <div class="col-12 md:col-4 flex flex-column gap-2">
              <label for="email" class="font-semibold text-sm">Email</label>
              <input id="email" type="email" pInputText formControlName="email" />
            </div>
            <div class="col-12 md:col-4 flex flex-column gap-2">
              <label for="mobilePhone" class="font-semibold text-sm">Téléphone GSM</label>
              <input id="mobilePhone" type="text" pInputText formControlName="mobilePhone" />
            </div>
            <div class="col-12 md:col-4 flex flex-column gap-2">
              <label for="birthdate" class="font-semibold text-sm">Date de naissance</label>
              <input id="birthdate" type="date" pInputText formControlName="birthdate" />
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
            <div class="col-12 md:col-4 flex flex-column gap-2">
              <label for="city" class="font-semibold text-sm">Ville</label>
              <input id="city" type="text" pInputText formControlName="city" />
            </div>
            <div class="col-12 md:col-4 flex flex-column gap-2">
              <label for="memberNumber" class="font-semibold text-sm">Numéro de Membre</label>
              <input id="memberNumber" type="number" pInputText formControlName="memberNumber" />
            </div>
          </div>

          <div class="flex gap-4 mt-2">
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
    :host ::ng-deep .p-datatable-sm .p-datatable-thead > tr > th {
      padding: 0.25rem 0.5rem !important;
      font-size: 0.875rem;
      background: #f8fafc;
      color: #334155;
      font-weight: 600;
      white-space: nowrap;
    }
    :host ::ng-deep .p-datatable-sm .p-datatable-tbody > tr > td {
      padding: 0.2rem 0.5rem !important;
      font-size: 0.875rem;
      line-height: 1.25;
    }
    :host ::ng-deep .p-datatable .p-datatable-tbody > tr {
      transition: background-color 0.15s;
    }
    :host ::ng-deep .p-datatable .p-datatable-tbody > tr.p-highlight {
      background: #fee2e2 !important;
      color: #991b1b !important;
    }
    :host ::ng-deep .p-datatable-sm .p-button.p-button-sm.p-button-rounded {
      width: 1.5rem !important;
      height: 1.5rem !important;
      padding: 0 !important;
    }
    :host ::ng-deep .p-datatable-sm .p-tag {
      font-size: 0.75rem;
      padding: 0.1rem 0.4rem;
      height: auto;
    }
    :host ::ng-deep .p-paginator {
      padding: 0.25rem 0.5rem !important;
    }
    :host ::ng-deep .p-paginator .p-paginator-page,
    :host ::ng-deep .p-paginator .p-paginator-next,
    :host ::ng-deep .p-paginator .p-paginator-last,
    :host ::ng-deep .p-paginator .p-paginator-first,
    :host ::ng-deep .p-paginator .p-paginator-prev {
      min-width: 1.75rem !important;
      height: 1.75rem !important;
      font-size: 0.85rem;
      padding: 0 !important;
      margin: 0 0.1rem;
    }
    :host ::ng-deep .p-paginator .p-dropdown {
      height: 1.75rem !important;
      font-size: 0.85rem;
    }
  `]
})
export class MemberListComponent implements OnInit {
  private memberService = inject(MemberService);
  private cotisationService = inject(CotisationService);
  private abonnementService = inject(AbonnementService);
  private travelService = inject(TravelService);
  private benevolatService = inject(BenevolatService);
  private messageService = inject(MessageService);
  private confirmationService = inject(ConfirmationService);
  private fb = inject(FormBuilder);

  members: Member[] = [];
  selectedMember: Member | null = null;
  selectedMemberCotisations: PersonCotisation[] = [];
  selectedMemberAbonnements: Abonnement[] = [];
  selectedMemberTravels: PersonTravel[] = [];
  selectedMemberBenevolats: Benevolat[] = [];

  totalElements = 0;
  pageSize = 10;
  currentPage = 0;
  currentSort = 'memberNumber,asc';
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
    address: [''],
    postalCode: [''],
    city: [''],
    birthdate: [''],
    memberNumber: [null],
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

        if (this.selectedMember) {
          const found = this.members.find(m => m.id === this.selectedMember?.id);
          if (found) {
            this.selectMember(found);
          }
        } else if (this.members.length > 0) {
          this.selectMember(this.members[0]);
        }
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  onSearch(): void {
    this.loadMembers({ first: 0, rows: this.pageSize });
  }

  onRowSelect(event: TableRowSelectEvent): void {
    if (event.data) {
      this.selectMember(event.data);
    }
  }

  selectMember(member: Member): void {
    this.selectedMember = member;
    if (member && member.id) {
      this.loadMemberDetailsData(member.id);
    } else {
      this.selectedMemberCotisations = [];
      this.selectedMemberAbonnements = [];
      this.selectedMemberTravels = [];
      this.selectedMemberBenevolats = [];
    }
  }

  loadMemberDetailsData(id: number): void {
    this.cotisationService.getCotisationsByMember(id).subscribe({
      next: (data) => (this.selectedMemberCotisations = data),
      error: () => (this.selectedMemberCotisations = [])
    });

    this.abonnementService.getAbonnementsByMember(id).subscribe({
      next: (data) => (this.selectedMemberAbonnements = data),
      error: () => (this.selectedMemberAbonnements = [])
    });

    this.travelService.getTravelsByPerson(id).subscribe({
      next: (data) => (this.selectedMemberTravels = data),
      error: () => (this.selectedMemberTravels = [])
    });

    this.benevolatService.getBenevolatsByPerson(id).subscribe({
      next: (data) => (this.selectedMemberBenevolats = data),
      error: () => (this.selectedMemberBenevolats = [])
    });
  }

  openNewMemberDialog(): void {
    this.isEditMode = false;
    this.selectedMemberId = null;
    this.memberForm.reset({
      name: '',
      firstname: '',
      email: '',
      mobilePhone: '',
      address: '',
      postalCode: '',
      city: '',
      birthdate: '',
      memberNumber: null,
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
    this.selectMember(member);
    this.memberForm.patchValue({
      name: member.name,
      firstname: member.firstname,
      email: member.email,
      mobilePhone: member.mobilePhone,
      address: member.address,
      postalCode: member.postalCode,
      city: member.city,
      birthdate: member.birthdate,
      memberNumber: member.memberNumber,
      redCard: member.redCard
    });
    this.memberDialog = true;
  }

  saveMember(): void {
    if (this.memberForm.invalid) {
      return;
    }

    this.saving = true;
    const formValue: MemberCreateUpdate = this.memberForm.value;

    if (this.isEditMode && this.selectedMemberId) {
      this.memberService.updateMember(this.selectedMemberId, formValue).subscribe({
        next: (updatedMember) => {
          this.saving = false;
          this.memberDialog = false;
          this.selectMember(updatedMember);
          this.messageService.add({ severity: 'success', summary: 'Succès', detail: 'Membre modifié avec succès' });
          this.loadMembers({ first: this.currentPage * this.pageSize, rows: this.pageSize });
        },
        error: () => {
          this.saving = false;
          this.messageService.add({ severity: 'error', summary: 'Erreur', detail: 'Échec de la modification' });
        }
      });
    } else {
      this.memberService.createMember(formValue).subscribe({
        next: (newMember) => {
          this.saving = false;
          this.memberDialog = false;
          this.selectMember(newMember);
          this.messageService.add({ severity: 'success', summary: 'Succès', detail: 'Nouveau membre créé avec succès' });
          this.loadMembers({ first: 0, rows: this.pageSize });
        },
        error: () => {
          this.saving = false;
          this.messageService.add({ severity: 'error', summary: 'Erreur', detail: 'Échec de la création' });
        }
      });
    }
  }

  confirmDeleteMember(member: Member): void {
    this.confirmationService.confirm({
      message: `Êtes-vous sûr de vouloir supprimer ${member.firstname} ${member.name} ?`,
      header: 'Confirmation de suppression',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Supprimer',
      rejectLabel: 'Annuler',
      acceptButtonStyleClass: 'p-button-danger',
      accept: () => {
        this.memberService.deleteMember(member.id).subscribe({
          next: () => {
            this.messageService.add({ severity: 'success', summary: 'Supprimé', detail: 'Le membre a été supprimé' });
            if (this.selectedMember?.id === member.id) {
              this.selectedMember = null;
              this.selectedMemberCotisations = [];
              this.selectedMemberAbonnements = [];
              this.selectedMemberTravels = [];
              this.selectedMemberBenevolats = [];
            }
            this.loadMembers({ first: this.currentPage * this.pageSize, rows: this.pageSize });
          }
        });
      }
    });
  }
}
