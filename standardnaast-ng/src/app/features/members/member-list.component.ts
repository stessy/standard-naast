import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { TableModule, TableLazyLoadEvent, TableRowSelectEvent } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { InputNumberModule } from 'primeng/inputnumber';
import { IconFieldModule } from 'primeng/iconfield';
import { InputIconModule } from 'primeng/inputicon';
import { DropdownModule } from 'primeng/dropdown';
import { DialogModule } from 'primeng/dialog';
import { CheckboxModule } from 'primeng/checkbox';
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
    InputNumberModule,
    IconFieldModule,
    InputIconModule,
    DropdownModule,
    DialogModule,
    CheckboxModule,
    TagModule,
    TooltipModule
  ],
  template: `
    <div class="members-page flex flex-column gap-3">
      <!-- Main Members Table Card with Header Toolbar -->
      <div class="surface-card p-2 sm:p-3 border-round-xl border-1 border-200 shadow-1 flex flex-column gap-2">
        <div class="flex flex-column md:flex-row justify-content-between align-items-start md:align-items-center gap-2 pb-1">
          <div class="flex flex-row align-items-center gap-3 flex-wrap">
            <div class="flex align-items-center gap-2">
              <h1 class="text-xl font-bold text-900 m-0">Gestion des Membres</h1>
              <span class="text-500 text-xs">({{ totalElements }} membres)</span>
            </div>
            <p-iconfield iconPosition="left" class="w-18rem max-w-full">
              <p-inputicon class="pi pi-search"></p-inputicon>
              <input
                pInputText
                type="text"
                placeholder="Rechercher par nom, prénom..."
                class="p-inputtext-sm w-full"
                [(ngModel)]="searchTerm"
                (input)="onSearch()" />
            </p-iconfield>
          </div>
          <button
            pButton
            type="button"
            label="Nouveau Membre"
            icon="pi pi-user-plus"
            class="p-button-danger p-button-sm font-bold white-space-nowrap"
            (click)="openNewMemberDialog()">
          </button>
        </div>

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
              <th style="width: 15%">Statut</th>
              <th style="width: 10%" class="text-center">Actions</th>
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
                    type="button"
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

      <!-- Selected Member Details & Sub-tables Section -->
      @if (selectedMember) {
        <!-- 1. Données du membre (Formulaire modifiable - Pleine largeur) -->
        <div class="surface-card p-3 sm:p-4 border-round-xl border-1 border-200 shadow-1 flex flex-column gap-3">
          <div class="flex flex-column sm:flex-row justify-content-between align-items-start sm:align-items-center gap-2 pb-2 border-bottom-1 border-200">
            <div class="flex align-items-center gap-2 flex-wrap">
              <i class="pi pi-id-card text-red-700 text-xl"></i>
              <h2 class="text-base sm:text-lg font-bold text-900 m-0">
                Données du membre
              </h2>
              @if (selectedMember.memberNumber) {
                <span class="bg-red-100 text-red-800 font-bold px-2 py-0 border-round text-xs">
                  N° {{ selectedMember.memberNumber }}
                </span>
              }
              @if (selectedMember.redCard) {
                <p-tag severity="danger" value="Carte Rouge"></p-tag>
              } @else {
                <p-tag severity="success" value="Actif"></p-tag>
              }
            </div>
            <div class="flex gap-2">
              <button
                pButton
                type="button"
                label="Supprimer"
                icon="pi pi-trash"
                class="p-button-outlined p-button-danger p-button-sm font-semibold"
                (click)="confirmDeleteMember(selectedMember)">
              </button>
              <button
                pButton
                type="button"
                label="Modifier"
                icon="pi pi-check"
                class="p-button-danger p-button-sm font-bold"
                [loading]="savingMember"
                (click)="saveSelectedMember()">
              </button>
            </div>
          </div>

          <!-- Formulaire Modifiable -->
          <form [formGroup]="memberForm" (ngSubmit)="saveSelectedMember()" class="flex flex-column gap-2">
            <!-- Row 1: Nom, Prénom, N° de membre -->
            <div class="grid -mt-1 -mb-1">
              <div class="col-12 md:col-4 py-1 flex flex-column gap-1">
                <label for="name" class="font-semibold text-xs text-700">Nom *</label>
                <input id="name" type="text" pInputText formControlName="name" class="p-inputtext-sm w-full" />
              </div>
              <div class="col-12 md:col-4 py-1 flex flex-column gap-1">
                <label for="firstname" class="font-semibold text-xs text-700">Prénom *</label>
                <input id="firstname" type="text" pInputText formControlName="firstname" class="p-inputtext-sm w-full" />
              </div>
              <div class="col-12 md:col-4 py-1 flex flex-column gap-1">
                <label for="memberNumber" class="font-semibold text-xs text-700">N° de membre</label>
                <input id="memberNumber" type="number" pInputText formControlName="memberNumber" [readonly]="true" class="p-inputtext-sm w-full surface-100 text-600 font-semibold cursor-not-allowed" />
              </div>
            </div>

            <!-- Row 2: Adresse, Code postal, Ville -->
            <div class="grid -mt-1 -mb-1">
              <div class="col-12 md:col-6 py-1 flex flex-column gap-1">
                <label for="address" class="font-semibold text-xs text-700">Adresse</label>
                <input id="address" type="text" pInputText formControlName="address" class="p-inputtext-sm w-full" />
              </div>
              <div class="col-12 md:col-3 py-1 flex flex-column gap-1">
                <label for="postalCode" class="font-semibold text-xs text-700">Code postal</label>
                <input id="postalCode" type="text" pInputText formControlName="postalCode" class="p-inputtext-sm w-full" />
              </div>
              <div class="col-12 md:col-3 py-1 flex flex-column gap-1">
                <label for="city" class="font-semibold text-xs text-700">Ville</label>
                <input id="city" type="text" pInputText formControlName="city" class="p-inputtext-sm w-full" />
              </div>
            </div>

            <!-- Row 3: Email, GSM, Date de naissance -->
            <div class="grid -mt-1 -mb-1">
              <div class="col-12 md:col-4 py-1 flex flex-column gap-1">
                <label for="email" class="font-semibold text-xs text-700">Email</label>
                <input id="email" type="email" pInputText formControlName="email" class="p-inputtext-sm w-full" />
              </div>
              <div class="col-12 md:col-4 py-1 flex flex-column gap-1">
                <label for="mobilePhone" class="font-semibold text-xs text-700">GSM</label>
                <input id="mobilePhone" type="text" pInputText formControlName="mobilePhone" class="p-inputtext-sm w-full" />
              </div>
              <div class="col-12 md:col-4 py-1 flex flex-column gap-1">
                <label for="birthdate" class="font-semibold text-xs text-700">Date de naissance</label>
                <input id="birthdate" type="date" pInputText formControlName="birthdate" class="p-inputtext-sm w-full" />
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
                [value]="selectedMemberAbonnements"
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
                [value]="selectedMemberCotisations"
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
      } @else {
        <div class="surface-card p-4 border-round-xl border-1 border-200 shadow-1 text-center text-500 text-sm">
          <i class="pi pi-info-circle mr-2"></i>
          Sélectionnez un membre dans le tableau ci-dessus pour afficher ses données et ses abonnements / cotisations.
        </div>
      }

      <!-- POPUP : Nouveau Membre -->
      <p-dialog
        [(visible)]="newMemberDialogVisible"
        header="Nouveau Membre"
        [modal]="true"
        [closable]="true"
        [dismissableMask]="true"
        [style]="{ width: '600px' }">
        <form [formGroup]="newMemberForm" (ngSubmit)="saveNewMember()" class="flex flex-column gap-3 mt-2">
          <div class="grid">
            <div class="col-12 md:col-6 flex flex-column gap-2">
              <label for="new_name" class="font-semibold text-sm">Nom *</label>
              <input id="new_name" type="text" pInputText formControlName="name" />
            </div>
            <div class="col-12 md:col-6 flex flex-column gap-2">
              <label for="new_firstname" class="font-semibold text-sm">Prénom *</label>
              <input id="new_firstname" type="text" pInputText formControlName="firstname" />
            </div>
          </div>

          <div class="grid">
            <div class="col-12 md:col-6 flex flex-column gap-2">
              <label for="new_email" class="font-semibold text-sm">Email</label>
              <input id="new_email" type="email" pInputText formControlName="email" />
            </div>
            <div class="col-12 md:col-6 flex flex-column gap-2">
              <label for="new_mobilePhone" class="font-semibold text-sm">GSM</label>
              <input id="new_mobilePhone" type="text" pInputText formControlName="mobilePhone" />
            </div>
          </div>

          <div class="grid">
            <div class="col-12 md:col-6 flex flex-column gap-2">
              <label for="new_birthdate" class="font-semibold text-sm">Date de naissance</label>
              <input id="new_birthdate" type="date" pInputText formControlName="birthdate" />
            </div>
            <div class="col-12 md:col-6 flex flex-column gap-2">
              <label for="new_memberNumber" class="font-semibold text-sm">N° de Membre</label>
              <input id="new_memberNumber" type="number" pInputText formControlName="memberNumber" />
            </div>
          </div>

          <div class="flex flex-column gap-2">
            <label for="new_address" class="font-semibold text-sm">Adresse</label>
            <input id="new_address" type="text" pInputText formControlName="address" />
          </div>

          <div class="grid">
            <div class="col-12 md:col-6 flex flex-column gap-2">
              <label for="new_postalCode" class="font-semibold text-sm">Code Postal</label>
              <input id="new_postalCode" type="text" pInputText formControlName="postalCode" />
            </div>
            <div class="col-12 md:col-6 flex flex-column gap-2">
              <label for="new_city" class="font-semibold text-sm">Ville</label>
              <input id="new_city" type="text" pInputText formControlName="city" />
            </div>
          </div>

          <div class="flex justify-content-end gap-2 mt-4">
            <button pButton type="button" label="Annuler" icon="pi pi-times" [text]="true" (click)="newMemberDialogVisible = false"></button>
            <button pButton type="submit" label="Enregistrer" icon="pi pi-check" class="p-button-danger font-bold" [loading]="savingMember"></button>
          </div>
        </form>
      </p-dialog>

      <!-- POPUP : Ajouter un Abonnement au membre -->
      <p-dialog
        [(visible)]="addAbonnementDialogVisible"
        [header]="'Ajouter un abonnement - ' + (selectedMember ? selectedMember.firstname + ' ' + selectedMember.name : '')"
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
            <p-checkbox formControlName="paye" [binary]="true" inputId="abo_paye"></p-checkbox>
            <label for="abo_paye" class="text-sm font-medium">Abonnement entièrement payé</label>
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
        [header]="'Ajouter une cotisation - ' + (selectedMember ? selectedMember.firstname + ' ' + selectedMember.name : '')"
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
            <p-checkbox formControlName="carteMembreEnvoyee" [binary]="true" inputId="cot_carteMembreEnvoyee"></p-checkbox>
            <label for="cot_carteMembreEnvoyee" class="text-sm font-medium">Carte membre envoyée</label>
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
  private seasonService = inject(SeasonService);
  private messageService = inject(MessageService);
  private confirmationService = inject(ConfirmationService);
  private fb = inject(FormBuilder);

  members: Member[] = [];
  selectedMember: Member | null = null;
  selectedMemberCotisations: PersonCotisation[] = [];
  selectedMemberAbonnements: Abonnement[] = [];
  seasons: Season[] = [];
  prices: AbonnementPrice[] = [];
  priceOptions: { label: string; value: number }[] = [];

  totalElements = 0;
  pageSize = 10;
  currentPage = 0;
  currentSort = 'memberNumber,asc';
  searchTerm = '';
  loading = false;
  savingMember = false;
  savingAbonnement = false;
  savingCotisation = false;

  newMemberDialogVisible = false;
  addAbonnementDialogVisible = false;
  addCotisationDialogVisible = false;

  statusOptions = [
    { label: 'Nouveau', value: AbonnementStatus.NEW },
    { label: 'Commandé', value: AbonnementStatus.ORDERED },
    { label: 'Reçu', value: AbonnementStatus.RECEIVED },
    { label: 'Distribué', value: AbonnementStatus.DISTRIBUTED }
  ];

  // Formulaire pour les données du membre sélectionné
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

  // Formulaire pour la création d'un nouveau membre (popup)
  newMemberForm: FormGroup = this.fb.group({
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

  // Formulaire pour l'ajout d'un abonnement (popup)
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

  // Formulaire pour l'ajout d'une cotisation (popup)
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
  }

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
    this.populateMemberForm(member);
    if (member && member.id) {
      this.loadMemberDetailsData(member.id);
    } else {
      this.selectedMemberCotisations = [];
      this.selectedMemberAbonnements = [];
    }
  }

  populateMemberForm(member: Member): void {
    this.memberForm.patchValue({
      name: member.name || '',
      firstname: member.firstname || '',
      email: member.email || '',
      phone: member.phone || '',
      mobilePhone: member.mobilePhone || '',
      address: member.address || '',
      postalCode: member.postalCode || '',
      city: member.city || '',
      birthdate: member.birthdate ? member.birthdate.substring(0, 10) : '',
      memberNumber: member.memberNumber ?? null,
      redCard: !!member.redCard,
      student: !!member.student
    });
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
  }

  saveSelectedMember(): void {
    if (!this.selectedMember || this.memberForm.invalid) {
      this.memberForm.markAllAsTouched();
      return;
    }

    this.savingMember = true;
    const formValue: MemberCreateUpdate = {
      ...this.memberForm.getRawValue(),
      memberNumber: this.selectedMember.memberNumber,
      phone: this.selectedMember.phone,
      student: this.selectedMember.student ?? false,
      redCard: this.selectedMember.redCard ?? false
    };

    this.memberService.updateMember(this.selectedMember.id, formValue).subscribe({
      next: (updatedMember) => {
        this.savingMember = false;
        this.selectedMember = updatedMember;
        this.populateMemberForm(updatedMember);
        const index = this.members.findIndex(m => m.id === updatedMember.id);
        if (index !== -1) {
          this.members[index] = updatedMember;
        }
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

  confirmDeleteMember(member: Member): void {
    this.confirmationService.confirm({
      message: `Voulez-vous vraiment supprimer le membre ${member.firstname} ${member.name} ?`,
      header: 'Confirmation de suppression',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Oui',
      rejectLabel: 'Non',
      acceptButtonStyleClass: 'p-button-danger',
      rejectButtonStyleClass: 'p-button-text',
      accept: () => {
        this.memberService.deleteMember(member.id).subscribe({
          next: () => {
            this.messageService.add({
              severity: 'success',
              summary: 'Succès',
              detail: 'Membre supprimé'
            });
            if (this.selectedMember?.id === member.id) {
              this.selectedMember = null;
            }
            this.loadMembers({ first: this.currentPage * this.pageSize, rows: this.pageSize });
          },
          error: () => {
            this.messageService.add({
              severity: 'error',
              summary: 'Erreur',
              detail: 'Impossible de supprimer le membre'
            });
          }
        });
      }
    });
  }

  openNewMemberDialog(): void {
    this.memberService.getNextMemberNumber().subscribe({
      next: (nextNum) => {
        this.newMemberForm.reset({
          name: '',
          firstname: '',
          email: '',
          phone: '',
          mobilePhone: '',
          address: '',
          postalCode: '',
          city: '',
          birthdate: '',
          memberNumber: nextNum,
          redCard: false,
          student: false
        });
        this.newMemberDialogVisible = true;
      },
      error: () => {
        this.newMemberForm.reset({
          name: '',
          firstname: '',
          email: '',
          phone: '',
          mobilePhone: '',
          address: '',
          postalCode: '',
          city: '',
          birthdate: '',
          memberNumber: null,
          redCard: false,
          student: false
        });
        this.newMemberDialogVisible = true;
      }
    });
  }

  saveNewMember(): void {
    if (this.newMemberForm.invalid) {
      this.newMemberForm.markAllAsTouched();
      return;
    }

    this.savingMember = true;
    const formValue: MemberCreateUpdate = this.newMemberForm.value;

    this.memberService.createMember(formValue).subscribe({
      next: (created) => {
        this.savingMember = false;
        this.newMemberDialogVisible = false;
        this.messageService.add({
          severity: 'success',
          summary: 'Succès',
          detail: 'Membre créé avec succès'
        });
        this.selectedMember = created;
        this.loadMembers({ first: 0, rows: this.pageSize });
      },
      error: () => {
        this.savingMember = false;
        this.messageService.add({
          severity: 'error',
          summary: 'Erreur',
          detail: 'Impossible de créer le membre'
        });
      }
    });
  }

  // --- Abonnements Popup & Actions ---
  openAddAbonnementDialog(): void {
    if (!this.selectedMember) return;

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
    if (this.abonnementForm.invalid || !this.selectedMember) {
      this.abonnementForm.markAllAsTouched();
      return;
    }

    this.savingAbonnement = true;
    const dto = {
      ...this.abonnementForm.value,
      personId: this.selectedMember.id
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
        if (this.selectedMember) {
          this.loadMemberDetailsData(this.selectedMember.id);
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
            if (this.selectedMember) {
              this.loadMemberDetailsData(this.selectedMember.id);
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

  // --- Cotisations Popup & Actions ---
  openAddCotisationDialog(): void {
    if (!this.selectedMember) return;

    const curSeason = this.seasonService.selectedSeason()?.id || (this.seasons.length > 0 ? this.seasons[0].id : '');

    this.cotisationForm.reset({
      seasonId: curSeason,
      datePaiement: '',
      carteMembreEnvoyee: false
    });

    this.addCotisationDialogVisible = true;
  }

  saveCotisation(): void {
    if (this.cotisationForm.invalid || !this.selectedMember) {
      this.cotisationForm.markAllAsTouched();
      return;
    }

    this.savingCotisation = true;
    const formVal = this.cotisationForm.value;
    const dto = {
      memberId: this.selectedMember.id,
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
        if (this.selectedMember) {
          this.loadMemberDetailsData(this.selectedMember.id);
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
            if (this.selectedMember) {
              this.loadMemberDetailsData(this.selectedMember.id);
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
