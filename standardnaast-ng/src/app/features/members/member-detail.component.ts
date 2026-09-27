import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { InputNumberModule } from 'primeng/inputnumber';
import { DropdownModule } from 'primeng/dropdown';
import { DialogModule } from 'primeng/dialog';
import { CheckboxModule } from 'primeng/checkbox';
import { TableModule } from 'primeng/table';
import { TagModule } from 'primeng/tag';
import { TooltipModule } from 'primeng/tooltip';
import { ConfirmationService, MessageService } from 'primeng/api';
import { MemberService } from '../../core/services/member.service';
import { CotisationService } from '../../core/services/cotisation.service';
import { AbonnementService } from '../../core/services/abonnement.service';
import { SeasonService } from '../../core/services/season.service';
import { Member, MemberCreateUpdate } from '../../core/models/member.model';
import { PersonCotisation } from '../../core/models/cotisation.model';
import { Abonnement, AbonnementPrice, AbonnementStatus } from '../../core/models/abonnement.model';
import { Season } from '../../core/models/season.model';

@Component({
  selector: 'app-member-detail',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    FormsModule,
    ReactiveFormsModule,
    ButtonModule,
    InputTextModule,
    InputNumberModule,
    DropdownModule,
    DialogModule,
    CheckboxModule,
    TableModule,
    TagModule,
    TooltipModule
  ],
  template: `
    <div class="member-detail-page flex flex-column gap-3" *ngIf="member">
      <!-- Top Bar -->
      <div class="flex justify-content-between align-items-center bg-white px-3 py-2 border-round-xl border-1 border-200 shadow-1">
        <div class="flex align-items-center gap-3">
          <button pButton icon="pi pi-arrow-left" [text]="true" routerLink="/members" class="p-button-rounded p-button-sm"></button>
          <div>
            <div class="flex align-items-center gap-2">
              <h1 class="text-xl font-bold text-900 m-0">{{ member.firstname }} {{ member.name }}</h1>
              @if (member.memberNumber) {
                <span class="bg-red-100 text-red-800 font-bold px-2 py-1 border-round text-sm">N° {{ member.memberNumber }}</span>
              }
              @if (member.redCard) {
                <p-tag severity="danger" value="Carte Rouge"></p-tag>
              } @else {
                <p-tag severity="success" value="Membre actif"></p-tag>
              }
            </div>
            <span class="text-500 text-xs">{{ member.email || 'Pas d\\'adresse email' }}</span>
          </div>
        </div>
      </div>

      <!-- 1. Données du membre (Formulaire modifiable - Pleine largeur) -->
      <div class="surface-card p-3 sm:p-4 border-round-xl border-1 border-200 shadow-1 flex flex-column gap-3">
        <div class="flex flex-column sm:flex-row justify-content-between align-items-start sm:align-items-center gap-2 pb-2 border-bottom-1 border-200">
          <div class="flex align-items-center gap-2 flex-wrap">
            <i class="pi pi-id-card text-red-700 text-xl"></i>
            <h2 class="text-base sm:text-lg font-bold text-900 m-0">
              Données du membre
            </h2>
          </div>
          <div class="flex gap-2">
            <button
              pButton
              type="button"
              label="Modifier"
              icon="pi pi-check"
              class="p-button-danger p-button-sm font-bold"
              [loading]="savingMember"
              (click)="saveMember()">
            </button>
          </div>
        </div>

        <!-- Formulaire Modifiable -->
        <form [formGroup]="memberForm" (ngSubmit)="saveMember()" class="flex flex-column gap-3">
          <!-- Row 1: Nom, Prénom, N° de membre -->
          <div class="grid">
            <div class="col-12 md:col-4 flex flex-column gap-1">
              <label for="detail_name" class="font-semibold text-xs text-700">Nom *</label>
              <input id="detail_name" type="text" pInputText formControlName="name" class="p-inputtext-sm w-full" />
            </div>
            <div class="col-12 md:col-4 flex flex-column gap-1">
              <label for="detail_firstname" class="font-semibold text-xs text-700">Prénom *</label>
              <input id="detail_firstname" type="text" pInputText formControlName="firstname" class="p-inputtext-sm w-full" />
            </div>
            <div class="col-12 md:col-4 flex flex-column gap-1">
              <label for="detail_memberNumber" class="font-semibold text-xs text-700">N° de membre</label>
              <input id="detail_memberNumber" type="number" pInputText formControlName="memberNumber" class="p-inputtext-sm w-full" />
            </div>
          </div>

          <!-- Row 2: Adresse, Code postal, Ville -->
          <div class="grid">
            <div class="col-12 md:col-6 flex flex-column gap-1">
              <label for="detail_address" class="font-semibold text-xs text-700">Adresse</label>
              <input id="detail_address" type="text" pInputText formControlName="address" class="p-inputtext-sm w-full" />
            </div>
            <div class="col-12 md:col-3 flex flex-column gap-1">
              <label for="detail_postalCode" class="font-semibold text-xs text-700">Code postal</label>
              <input id="detail_postalCode" type="text" pInputText formControlName="postalCode" class="p-inputtext-sm w-full" />
            </div>
            <div class="col-12 md:col-3 flex flex-column gap-1">
              <label for="detail_city" class="font-semibold text-xs text-700">Ville</label>
              <input id="detail_city" type="text" pInputText formControlName="city" class="p-inputtext-sm w-full" />
            </div>
          </div>

          <!-- Row 3: Email, GSM, Téléphone -->
          <div class="grid">
            <div class="col-12 md:col-4 flex flex-column gap-1">
              <label for="detail_email" class="font-semibold text-xs text-700">Email</label>
              <input id="detail_email" type="email" pInputText formControlName="email" class="p-inputtext-sm w-full" />
            </div>
            <div class="col-12 md:col-4 flex flex-column gap-1">
              <label for="detail_mobilePhone" class="font-semibold text-xs text-700">GSM</label>
              <input id="detail_mobilePhone" type="text" pInputText formControlName="mobilePhone" class="p-inputtext-sm w-full" />
            </div>
            <div class="col-12 md:col-4 flex flex-column gap-1">
              <label for="detail_phone" class="font-semibold text-xs text-700">Téléphone</label>
              <input id="detail_phone" type="text" pInputText formControlName="phone" class="p-inputtext-sm w-full" />
            </div>
          </div>

          <!-- Row 4: Date de naissance, Étudiant, Red Card -->
          <div class="grid align-items-center">
            <div class="col-12 md:col-4 flex flex-column gap-1">
              <label for="detail_birthdate" class="font-semibold text-xs text-700">Date de naissance</label>
              <input id="detail_birthdate" type="date" pInputText formControlName="birthdate" class="p-inputtext-sm w-full" />
            </div>
            <div class="col-12 md:col-4 flex align-items-center gap-2 pt-2 md:pt-4">
              <p-checkbox formControlName="student" [binary]="true" inputId="detail_student"></p-checkbox>
              <label for="detail_student" class="text-sm font-medium">Étudiant</label>
            </div>
            <div class="col-12 md:col-4 flex align-items-center gap-2 pt-2 md:pt-4">
              <p-checkbox formControlName="redCard" [binary]="true" inputId="detail_redCard"></p-checkbox>
              <label for="detail_redCard" class="text-sm font-medium text-red-600">Carte Rouge</label>
            </div>
          </div>
        </form>
      </div>

      <!-- 2. Tableaux Abonnements et Cotisations (sur la même ligne) -->
      <div class="grid">
        <!-- Abonnements du membre (Gauche) -->
        <div class="col-12 lg:col-6">
          <div class="surface-card p-3 border-round-xl border-1 border-200 shadow-1 flex flex-column gap-2 h-full">
            <div class="flex justify-content-between align-items-center pb-2 border-bottom-1 border-200">
              <div class="flex align-items-center gap-2">
                <i class="pi pi-ticket text-red-700 font-bold"></i>
                <h3 class="text-sm sm:text-base font-bold text-900 m-0">Abonnements du membre</h3>
              </div>
              <button
                pButton
                type="button"
                label="Ajouter"
                icon="pi pi-plus"
                class="p-button-danger p-button-sm font-semibold"
                (click)="openAddAbonnementDialog()">
              </button>
            </div>

            <p-table
              [value]="abonnements"
              [scrollable]="true"
              scrollHeight="220px"
              [paginator]="true"
              [rows]="5"
              [rowsPerPageOptions]="[5, 10, 20]"
              responsiveLayout="stack"
              styleClass="p-datatable-sm p-datatable-striped">
              <ng-template pTemplate="header">
                <tr>
                  <th>Saison</th>
                  <th>Bloc</th>
                  <th>Rang</th>
                  <th>Place</th>
                  <th>Réduc (€)</th>
                  <th>Statut</th>
                  <th class="text-center" style="width: 3rem"></th>
                </tr>
              </ng-template>
              <ng-template pTemplate="body" let-abo>
                <tr>
                  <td><span class="font-bold">{{ abo.seasonId }}</span></td>
                  <td>{{ abo.bloc || abo.abonnementPrice?.bloc || '-' }}</td>
                  <td>{{ abo.rang || '-' }}</td>
                  <td>{{ abo.place || '-' }}</td>
                  <td>{{ (abo.reduction != null ? abo.reduction : 0) | currency:'EUR':'symbol':'1.2-2':'fr' }}</td>
                  <td>
                    <p-tag [severity]="getStatusSeverity(abo.abonnementStatus || abo.status)" [value]="abo.abonnementStatus || abo.status"></p-tag>
                  </td>
                  <td class="text-center">
                    <button
                      pButton
                      type="button"
                      icon="pi pi-trash"
                      class="p-button-rounded p-button-text p-button-sm p-button-danger"
                      (click)="confirmDeleteAbonnement(abo)"
                      pTooltip="Supprimer l'abonnement">
                    </button>
                  </td>
                </tr>
              </ng-template>
              <ng-template pTemplate="emptymessage">
                <tr>
                  <td colspan="7" class="text-center p-3 text-500">Aucun abonnement pour ce membre.</td>
                </tr>
              </ng-template>
            </p-table>
          </div>
        </div>

        <!-- Cotisations membre (Droite) -->
        <div class="col-12 lg:col-6">
          <div class="surface-card p-3 border-round-xl border-1 border-200 shadow-1 flex flex-column gap-2 h-full">
            <div class="flex justify-content-between align-items-center pb-2 border-bottom-1 border-200">
              <div class="flex align-items-center gap-2">
                <i class="pi pi-credit-card text-red-700 font-bold"></i>
                <h3 class="text-sm sm:text-base font-bold text-900 m-0">Cotisations membre</h3>
              </div>
              <button
                pButton
                type="button"
                label="Ajouter"
                icon="pi pi-plus"
                class="p-button-danger p-button-sm font-semibold"
                (click)="openAddCotisationDialog()">
              </button>
            </div>

            <p-table
              [value]="cotisations"
              [scrollable]="true"
              scrollHeight="220px"
              [paginator]="true"
              [rows]="5"
              [rowsPerPageOptions]="[5, 10, 20]"
              responsiveLayout="stack"
              styleClass="p-datatable-sm p-datatable-striped">
              <ng-template pTemplate="header">
                <tr>
                  <th>Saison</th>
                  <th>Date Paiement</th>
                  <th>Carte envoyée</th>
                  <th class="text-center" style="width: 3rem"></th>
                </tr>
              </ng-template>
              <ng-template pTemplate="body" let-cot>
                <tr>
                  <td><span class="font-bold">{{ cot.seasonId }}</span></td>
                  <td>{{ cot.datePaiement ? (cot.datePaiement | date:'dd/MM/yyyy') : 'Non payé' }}</td>
                  <td>
                    <p-tag [severity]="cot.carteMembreEnvoyee ? 'success' : 'warning'" [value]="cot.carteMembreEnvoyee ? 'Oui' : 'Non'"></p-tag>
                  </td>
                  <td class="text-center">
                    <button
                      pButton
                      type="button"
                      icon="pi pi-trash"
                      class="p-button-rounded p-button-text p-button-sm p-button-danger"
                      (click)="confirmDeleteCotisation(cot)"
                      pTooltip="Supprimer la cotisation">
                    </button>
                  </td>
                </tr>
              </ng-template>
              <ng-template pTemplate="emptymessage">
                <tr>
                  <td colspan="4" class="text-center p-3 text-500">Aucune cotisation pour ce membre.</td>
                </tr>
              </ng-template>
            </p-table>
          </div>
        </div>
      </div>

      <!-- POPUP : Ajouter un Abonnement au membre -->
      <p-dialog
        [(visible)]="addAbonnementDialogVisible"
        [header]="'Ajouter un abonnement - ' + (member ? member.firstname + ' ' + member.name : '')"
        [modal]="true"
        [closable]="true"
        [dismissableMask]="true"
        [style]="{ width: '600px' }">
        <form [formGroup]="abonnementForm" (ngSubmit)="saveAbonnement()" class="flex flex-column gap-3 mt-2">
          <div class="grid">
            <div class="col-12 md:col-6 flex flex-column gap-2">
              <label class="font-semibold text-sm">Saison *</label>
              <p-dropdown
                [options]="seasons"
                optionLabel="id"
                optionValue="id"
                formControlName="seasonId"
                placeholder="Sélectionner une saison"
                (onChange)="onAbonnementSeasonChange($event.value)">
              </p-dropdown>
            </div>
            <div class="col-12 md:col-6 flex flex-column gap-2">
              <label class="font-semibold text-sm">Statut</label>
              <p-dropdown [options]="statusOptions" formControlName="status"></p-dropdown>
            </div>
          </div>

          <div class="flex flex-column gap-2">
            <label class="font-semibold text-sm">Tarif Abonnement *</label>
            <p-dropdown
              [options]="priceOptions"
              optionLabel="label"
              optionValue="value"
              formControlName="abonnementPriceId"
              placeholder="Choisir un tarif">
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
            <p-checkbox formControlName="paye" [binary]="true" inputId="detail_abo_paye"></p-checkbox>
            <label for="detail_abo_paye" class="text-sm font-medium">Abonnement entièrement payé</label>
          </div>

          <div class="flex justify-content-end gap-2 mt-4">
            <button pButton type="button" label="Annuler" icon="pi pi-times" [text]="true" (click)="addAbonnementDialogVisible = false"></button>
            <button pButton type="submit" label="Enregistrer" icon="pi pi-check" class="p-button-danger font-bold" [loading]="savingAbonnement"></button>
          </div>
        </form>
      </p-dialog>

      <!-- POPUP : Ajouter une Cotisation au membre -->
      <p-dialog
        [(visible)]="addCotisationDialogVisible"
        [header]="'Ajouter une cotisation - ' + (member ? member.firstname + ' ' + member.name : '')"
        [modal]="true"
        [closable]="true"
        [dismissableMask]="true"
        [style]="{ width: '480px' }">
        <form [formGroup]="cotisationForm" (ngSubmit)="saveCotisation()" class="flex flex-column gap-3 mt-2">
          <div class="flex flex-column gap-2">
            <label class="font-semibold text-sm">Saison *</label>
            <p-dropdown
              [options]="seasons"
              optionLabel="id"
              optionValue="id"
              formControlName="seasonId"
              placeholder="Sélectionner une saison">
            </p-dropdown>
          </div>

          <div class="flex flex-column gap-2">
            <label class="font-semibold text-sm">Date de paiement</label>
            <input type="date" pInputText formControlName="datePaiement" />
          </div>

          <div class="flex align-items-center gap-2 mt-2">
            <p-checkbox formControlName="carteMembreEnvoyee" [binary]="true" inputId="detail_cot_carteMembreEnvoyee"></p-checkbox>
            <label for="detail_cot_carteMembreEnvoyee" class="text-sm font-medium">Carte membre envoyée</label>
          </div>

          <div class="flex justify-content-end gap-2 mt-4">
            <button pButton type="button" label="Annuler" icon="pi pi-times" [text]="true" (click)="addCotisationDialogVisible = false"></button>
            <button pButton type="submit" label="Enregistrer" icon="pi pi-check" class="p-button-danger font-bold" [loading]="savingCotisation"></button>
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
    :host ::ng-deep .p-paginator {
      padding: 0.25rem 0.5rem !important;
    }
  `]
})
export class MemberDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private memberService = inject(MemberService);
  private cotisationService = inject(CotisationService);
  private abonnementService = inject(AbonnementService);
  private seasonService = inject(SeasonService);
  private messageService = inject(MessageService);
  private confirmationService = inject(ConfirmationService);
  private fb = inject(FormBuilder);

  member: Member | null = null;
  cotisations: PersonCotisation[] = [];
  abonnements: Abonnement[] = [];
  seasons: Season[] = [];
  prices: AbonnementPrice[] = [];
  priceOptions: { label: string; value: number }[] = [];

  savingMember = false;
  savingAbonnement = false;
  savingCotisation = false;

  addAbonnementDialogVisible = false;
  addCotisationDialogVisible = false;

  statusOptions = [
    { label: 'Nouveau', value: AbonnementStatus.NEW },
    { label: 'Commandé', value: AbonnementStatus.ORDERED },
    { label: 'Reçu', value: AbonnementStatus.RECEIVED },
    { label: 'Distribué', value: AbonnementStatus.DISTRIBUTED }
  ];

  memberForm: FormGroup = this.fb.group({
    name: ['', Validators.required],
    firstname: ['', Validators.required],
    email: [''],
    phone: [''],
    mobilePhone: [''],
    address: [''],
    postalCode: [''],
    city: [''],
    birthdate: [''],
    memberNumber: [null],
    redCard: [false],
    student: [false]
  });

  abonnementForm: FormGroup = this.fb.group({
    seasonId: ['', Validators.required],
    abonnementPriceId: [null, Validators.required],
    rang: [''],
    place: [''],
    montantPaye: [0],
    reduction: [0],
    paye: [false],
    status: [AbonnementStatus.NEW]
  });

  cotisationForm: FormGroup = this.fb.group({
    seasonId: ['', Validators.required],
    datePaiement: [''],
    carteMembreEnvoyee: [false]
  });

  ngOnInit(): void {
    this.seasonService.getAllSeasons().subscribe({
      next: (data) => (this.seasons = data),
      error: () => (this.seasons = [])
    });

    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (id) {
      this.loadMemberData(id);
    }
  }

  loadMemberData(id: number): void {
    this.memberService.getMemberById(id).subscribe({
      next: (data) => {
        this.member = data;
        this.memberForm.patchValue({
          name: data.name || '',
          firstname: data.firstname || '',
          email: data.email || '',
          phone: data.phone || '',
          mobilePhone: data.mobilePhone || '',
          address: data.address || '',
          postalCode: data.postalCode || '',
          city: data.city || '',
          birthdate: data.birthdate ? data.birthdate.substring(0, 10) : '',
          memberNumber: data.memberNumber ?? null,
          redCard: !!data.redCard,
          student: !!data.student
        });
      }
    });

    this.cotisationService.getCotisationsByMember(id).subscribe({
      next: (data) => (this.cotisations = data),
      error: () => (this.cotisations = [])
    });

    this.abonnementService.getAbonnementsByMember(id).subscribe({
      next: (data) => (this.abonnements = data),
      error: () => (this.abonnements = [])
    });
  }

  saveMember(): void {
    if (!this.member || this.memberForm.invalid) {
      this.memberForm.markAllAsTouched();
      return;
    }

    this.savingMember = true;
    const formValue: MemberCreateUpdate = this.memberForm.value;

    this.memberService.updateMember(this.member.id, formValue).subscribe({
      next: (updatedMember) => {
        this.savingMember = false;
        this.member = updatedMember;
        this.messageService.add({
          severity: 'success',
          summary: 'Succès',
          detail: 'Données du membre modifiées avec succès'
        });
      },
      error: () => {
        this.savingMember = false;
        this.messageService.add({
          severity: 'error',
          summary: 'Erreur',
          detail: 'Impossible de modifier les données du membre'
        });
      }
    });
  }

  openAddAbonnementDialog(): void {
    if (!this.member) return;

    const curSeason = this.seasonService.selectedSeason()?.id || (this.seasons.length > 0 ? this.seasons[0].id : '');

    this.abonnementForm.reset({
      seasonId: curSeason,
      abonnementPriceId: null,
      rang: '',
      place: '',
      montantPaye: 0,
      reduction: 0,
      paye: false,
      status: AbonnementStatus.NEW
    });

    if (curSeason) {
      this.loadPricesForSeason(curSeason);
    } else {
      this.prices = [];
      this.priceOptions = [];
    }

    this.addAbonnementDialogVisible = true;
  }

  onAbonnementSeasonChange(seasonId: string): void {
    if (seasonId) {
      this.loadPricesForSeason(seasonId);
    } else {
      this.prices = [];
      this.priceOptions = [];
    }
  }

  loadPricesForSeason(seasonId: string): void {
    this.abonnementService.getPricesBySeason(seasonId).subscribe({
      next: (prices) => {
        this.prices = prices;
        this.priceOptions = prices.map(p => ({
          label: `${p.bloc || ''} - ${p.personType || ''} (${p.price} €)`,
          value: p.id
        }));
        if (prices.length > 0 && !this.abonnementForm.get('abonnementPriceId')?.value) {
          this.abonnementForm.patchValue({ abonnementPriceId: prices[0].id });
        }
      },
      error: () => {
        this.prices = [];
        this.priceOptions = [];
      }
    });
  }

  saveAbonnement(): void {
    if (this.abonnementForm.invalid || !this.member) {
      this.abonnementForm.markAllAsTouched();
      return;
    }

    this.savingAbonnement = true;
    const dto = {
      ...this.abonnementForm.value,
      personId: this.member.id
    };

    this.abonnementService.createAbonnement(dto).subscribe({
      next: () => {
        this.savingAbonnement = false;
        this.addAbonnementDialogVisible = false;
        this.messageService.add({
          severity: 'success',
          summary: 'Succès',
          detail: 'Abonnement ajouté avec succès'
        });
        if (this.member) {
          this.loadMemberData(this.member.id);
        }
      },
      error: () => {
        this.savingAbonnement = false;
        this.messageService.add({
          severity: 'error',
          summary: 'Erreur',
          detail: "Impossible d'ajouter l'abonnement"
        });
      }
    });
  }

  confirmDeleteAbonnement(abo: Abonnement): void {
    this.confirmationService.confirm({
      message: `Supprimer l'abonnement pour la saison ${abo.seasonId} ?`,
      header: "Suppression de l'abonnement",
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Oui',
      rejectLabel: 'Non',
      acceptButtonStyleClass: 'p-button-danger',
      rejectButtonStyleClass: 'p-button-text',
      accept: () => {
        this.abonnementService.deleteAbonnement(abo.id).subscribe({
          next: () => {
            this.messageService.add({
              severity: 'success',
              summary: 'Succès',
              detail: 'Abonnement supprimé'
            });
            if (this.member) {
              this.loadMemberData(this.member.id);
            }
          },
          error: () => {
            this.messageService.add({
              severity: 'error',
              summary: 'Erreur',
              detail: "Impossible de supprimer l'abonnement"
            });
          }
        });
      }
    });
  }

  openAddCotisationDialog(): void {
    if (!this.member) return;

    const curSeason = this.seasonService.selectedSeason()?.id || (this.seasons.length > 0 ? this.seasons[0].id : '');

    this.cotisationForm.reset({
      seasonId: curSeason,
      datePaiement: '',
      carteMembreEnvoyee: false
    });

    this.addCotisationDialogVisible = true;
  }

  saveCotisation(): void {
    if (this.cotisationForm.invalid || !this.member) {
      this.cotisationForm.markAllAsTouched();
      return;
    }

    this.savingCotisation = true;
    const formVal = this.cotisationForm.value;
    const dto = {
      memberId: this.member.id,
      seasonId: formVal.seasonId,
      datePaiement: formVal.datePaiement || undefined,
      carteMembreEnvoyee: !!formVal.carteMembreEnvoyee
    };

    this.cotisationService.registerMemberCotisation(dto).subscribe({
      next: () => {
        this.savingCotisation = false;
        this.addCotisationDialogVisible = false;
        this.messageService.add({
          severity: 'success',
          summary: 'Succès',
          detail: 'Cotisation enregistrée avec succès'
        });
        if (this.member) {
          this.loadMemberData(this.member.id);
        }
      },
      error: () => {
        this.savingCotisation = false;
        this.messageService.add({
          severity: 'error',
          summary: 'Erreur',
          detail: "Impossible d'enregistrer la cotisation"
        });
      }
    });
  }

  confirmDeleteCotisation(cot: PersonCotisation): void {
    this.confirmationService.confirm({
      message: `Supprimer la cotisation pour la saison ${cot.seasonId} ?`,
      header: 'Suppression de la cotisation',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Oui',
      rejectLabel: 'Non',
      acceptButtonStyleClass: 'p-button-danger',
      rejectButtonStyleClass: 'p-button-text',
      accept: () => {
        this.cotisationService.deleteMemberCotisation(cot.id).subscribe({
          next: () => {
            this.messageService.add({
              severity: 'success',
              summary: 'Succès',
              detail: 'Cotisation supprimée'
            });
            if (this.member) {
              this.loadMemberData(this.member.id);
            }
          },
          error: () => {
            this.messageService.add({
              severity: 'error',
              summary: 'Erreur',
              detail: 'Impossible de supprimer la cotisation'
            });
          }
        });
      }
    });
  }

  getStatusSeverity(status: string | undefined): 'success' | 'info' | 'warn' | 'danger' | 'secondary' | 'contrast' | undefined {
    switch (status) {
      case AbonnementStatus.DISTRIBUTED:
      case 'DISTRIBUTED':
        return 'success';
      case AbonnementStatus.RECEIVED:
      case 'RECEIVED':
        return 'info';
      case AbonnementStatus.ORDERED:
      case 'ORDERED':
        return 'warn';
      default:
        return 'secondary';
    }
  }
}
