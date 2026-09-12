import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { TableModule, TableLazyLoadEvent } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { DropdownModule } from 'primeng/dropdown';
import { DialogModule } from 'primeng/dialog';
import { ConfirmationService, MessageService } from 'primeng/api';
import { TravelService } from '../../core/services/travel.service';
import { SeasonService } from '../../core/services/season.service';
import { MatchService } from '../../core/services/match.service';
import { MemberService } from '../../core/services/member.service';
import { PersonTravel, TravelPrice } from '../../core/models/travel.model';
import { Match } from '../../core/models/match.model';
import { Member } from '../../core/models/member.model';

@Component({
  selector: 'app-travel-list',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    TableModule,
    ButtonModule,
    DropdownModule,
    DialogModule
  ],
  template: `
    <div class="travels-page flex flex-column gap-2">
      <div class="flex flex-column sm:flex-row justify-content-between align-items-start sm:align-items-center gap-2 bg-white px-3 py-2 border-round-xl border-1 border-200 shadow-1">
        <div>
          <h1 class="text-xl font-bold text-900 m-0">Gestion des Déplacements</h1>
          <p class="text-500 text-xs m-0 mt-1">Organisation des cars et inscriptions pour les matchs en déplacement</p>
        </div>
        <button pButton label="Inscrire un Passager" icon="pi pi-plus" class="p-button-danger p-button-sm font-bold" (click)="openNewDialog()"></button>
      </div>

      <div class="surface-card p-2 sm:p-3 border-round-xl border-1 border-200 shadow-1">
        <p-table
          [value]="travels"
          [lazy]="true"
          (onLazyLoad)="loadTravels($event)"
          [paginator]="true"
          [rows]="pageSize"
          [totalRecords]="totalElements"
          [loading]="loading"
          responsiveLayout="stack"
          styleClass="p-datatable-sm p-datatable-striped">
          <ng-template pTemplate="header">
            <tr>
              <th>Date Match</th>
              <th>Adversaire</th>
              <th>Passager</th>
              <th>Membre</th>
              <th>Montant Payé</th>
              <th class="text-center">Actions</th>
            </tr>
          </ng-template>
          <ng-template pTemplate="body" let-travel>
            <tr>
              <td>{{ travel.dateMatch | date:'dd/MM/yyyy' }}</td>
              <td><span class="font-semibold">{{ travel.opponentName }}</span></td>
              <td><span class="font-medium text-900">{{ travel.firstName }} {{ travel.lastName }}</span></td>
              <td>{{ travel.isMember ? 'Oui' : 'Non' }}</td>
              <td>{{ travel.amountPaid | currency:'EUR':'symbol':'1.2-2':'fr' }}</td>
              <td class="text-center">
                <button pButton icon="pi pi-trash" class="p-button-rounded p-button-text p-button-sm p-button-danger" (click)="confirmDelete(travel)"></button>
              </td>
            </tr>
          </ng-template>
          <ng-template pTemplate="emptymessage">
            <tr><td colspan="6" class="text-center p-3 text-500">Aucun passager enregistré pour cette saison.</td></tr>
          </ng-template>
        </p-table>
      </div>

      <!-- Travel Inscription Dialog -->
      <p-dialog [(visible)]="dialogVisible" header="Inscrire un passager" [modal]="true" [style]="{ width: '500px' }">
        <form [formGroup]="form" (ngSubmit)="saveTravel()" class="flex flex-column gap-3 mt-2">
          <div class="flex flex-column gap-2">
            <label class="font-semibold text-sm">Match *</label>
            <p-dropdown [options]="matches" optionLabel="opponentName" optionValue="id" formControlName="matchId" placeholder="Sélectionner un match"></p-dropdown>
          </div>

          <div class="flex flex-column gap-2">
            <label class="font-semibold text-sm">Passager / Membre *</label>
            <p-dropdown [options]="members" optionLabel="name" optionValue="id" formControlName="personId" [filter]="true" placeholder="Sélectionner une personne"></p-dropdown>
          </div>

          <div class="flex flex-column gap-2">
            <label class="font-semibold text-sm">Tarif Voyage *</label>
            <p-dropdown [options]="prices" optionLabel="montant" optionValue="id" formControlName="travelPriceId" placeholder="Sélectionner un tarif"></p-dropdown>
          </div>

          <div class="flex justify-content-end gap-2 mt-4">
            <button pButton type="button" label="Annuler" icon="pi pi-times" [text]="true" (click)="dialogVisible = false"></button>
            <button pButton type="submit" label="Inscrire" icon="pi pi-check" class="p-button-danger font-bold"></button>
          </div>
        </form>
      </p-dialog>
    </div>
  `
})
export class TravelListComponent implements OnInit {
  private travelService = inject(TravelService);
  private seasonService = inject(SeasonService);
  private matchService = inject(MatchService);
  private memberService = inject(MemberService);
  private messageService = inject(MessageService);
  private confirmationService = inject(ConfirmationService);
  private fb = inject(FormBuilder);

  travels: PersonTravel[] = [];
  matches: Match[] = [];
  members: Member[] = [];
  prices: TravelPrice[] = [];
  totalElements = 0;
  pageSize = 20;
  loading = false;
  dialogVisible = false;

  form: FormGroup = this.fb.group({
    matchId: [null, Validators.required],
    personId: [null, Validators.required],
    travelPriceId: [null, Validators.required]
  });

  ngOnInit(): void {
    const curSeason = this.seasonService.selectedSeason()?.id;
    if (curSeason) {
      this.matchService.getMatchesBySeason(curSeason).subscribe({
        next: (res) => this.matches = res
      });
      this.travelService.getTravelPricesBySeason(curSeason).subscribe({
        next: (pr) => this.prices = pr
      });
    }

    this.memberService.getMembers(undefined, 0, 1000).subscribe({
      next: (res) => this.members = res.content
    });
  }

  loadTravels(event: TableLazyLoadEvent): void {
    this.loading = true;
    const page = event.first ? Math.floor(event.first / (event.rows || this.pageSize)) : 0;
    const size = event.rows || this.pageSize;
    const curSeason = this.seasonService.selectedSeason()?.id;

    this.travelService.searchTravels(curSeason, undefined, undefined, page, size).subscribe({
      next: (res) => {
        this.travels = res.content;
        this.totalElements = res.totalElements;
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  openNewDialog(): void {
    this.form.reset();
    this.dialogVisible = true;
  }

  saveTravel(): void {
    if (this.form.invalid) return;

    this.travelService.registerPersonTravel(this.form.value).subscribe({
      next: () => {
        this.dialogVisible = false;
        this.messageService.add({ severity: 'success', summary: 'Succès', detail: 'Passager inscrit' });
        this.loadTravels({ first: 0, rows: this.pageSize });
      }
    });
  }

  confirmDelete(travel: PersonTravel): void {
    this.confirmationService.confirm({
      message: `Supprimer l'inscription de ${travel.firstName} ${travel.lastName} ?`,
      acceptButtonStyleClass: 'p-button-danger',
      accept: () => {
        this.travelService.removePersonTravel(travel.id).subscribe({
          next: () => {
            this.messageService.add({ severity: 'success', summary: 'Supprimé', detail: 'Inscription supprimée' });
            this.loadTravels({ first: 0, rows: this.pageSize });
          }
        });
      }
    });
  }
}
