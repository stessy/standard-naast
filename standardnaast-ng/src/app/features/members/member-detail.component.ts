import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { CardModule } from 'primeng/card';
import { ButtonModule } from 'primeng/button';
import { TabViewModule } from 'primeng/tabview';
import { TableModule } from 'primeng/table';
import { TagModule } from 'primeng/tag';
import { MemberService } from '../../core/services/member.service';
import { CotisationService } from '../../core/services/cotisation.service';
import { AbonnementService } from '../../core/services/abonnement.service';
import { TravelService } from '../../core/services/travel.service';
import { BenevolatService } from '../../core/services/benevolat.service';
import { Member } from '../../core/models/member.model';
import { PersonCotisation } from '../../core/models/cotisation.model';
import { Abonnement } from '../../core/models/abonnement.model';
import { PersonTravel } from '../../core/models/travel.model';
import { Benevolat } from '../../core/models/benevolat.model';

@Component({
  selector: 'app-member-detail',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    CardModule,
    ButtonModule,
    TabViewModule,
    TableModule,
    TagModule
  ],
  template: `
    <div class="member-detail-page flex flex-column gap-4" *ngIf="member">
      <!-- Top Bar -->
      <div class="flex justify-content-between align-items-center bg-white p-4 border-round-xl border-1 border-200 shadow-1">
        <div class="flex align-items-center gap-3">
          <button pButton icon="pi pi-arrow-left" [text]="true" routerLink="/members" class="p-button-rounded"></button>
          <div>
            <div class="flex align-items-center gap-2">
              <h1 class="text-2xl font-bold text-900 m-0">{{ member.firstname }} {{ member.name }}</h1>
              @if (member.memberNumber) {
                <span class="bg-red-100 text-red-800 font-bold px-2 py-1 border-round text-sm">N° {{ member.memberNumber }}</span>
              }
            </div>
            <span class="text-500 text-sm">{{ member.email || 'Pas d\\'adresse email' }}</span>
          </div>
        </div>
      </div>

      <!-- Member Tabs -->
      <div class="surface-card p-4 border-round-xl border-1 border-200 shadow-1">
        <p-tabView>
          <!-- Identity Tab -->
          <p-tabPanel header="Identité" leftIcon="pi pi-user">
            <div class="grid p-3">
              <div class="col-12 md:col-6 flex flex-column gap-3">
                <div>
                  <span class="text-500 text-xs font-semibold uppercase">Nom & Prénom</span>
                  <div class="text-900 font-medium text-base">{{ member.firstname }} {{ member.name }}</div>
                </div>
                <div>
                  <span class="text-500 text-xs font-semibold uppercase">Date de naissance</span>
                  <div class="text-900 font-medium text-base">{{ member.birthdate ? (member.birthdate | date:'dd/MM/yyyy') : '-' }}</div>
                </div>
                <div>
                  <span class="text-500 text-xs font-semibold uppercase">Téléphone GSM</span>
                  <div class="text-900 font-medium text-base">{{ member.mobilePhone || '-' }}</div>
                </div>
              </div>

              <div class="col-12 md:col-6 flex flex-column gap-3">
                <div>
                  <span class="text-500 text-xs font-semibold uppercase">Adresse</span>
                  <div class="text-900 font-medium text-base">{{ member.address || '-' }}</div>
                </div>
                <div>
                  <span class="text-500 text-xs font-semibold uppercase">Code Postal & Ville</span>
                  <div class="text-900 font-medium text-base">{{ member.postalCode }} {{ member.city }}</div>
                </div>
                <div>
                  <span class="text-500 text-xs font-semibold uppercase">Statut</span>
                  <div class="mt-1">
                    @if (member.redCard) {
                      <p-tag severity="danger" value="Carte Rouge"></p-tag>
                    } @else if (member.student) {
                      <p-tag severity="info" value="Étudiant"></p-tag>
                    } @else {
                      <p-tag severity="success" value="Membre actif"></p-tag>
                    }
                  </div>
                </div>
              </div>
            </div>
          </p-tabPanel>

          <!-- Cotisations Tab -->
          <p-tabPanel header="Cotisations" leftIcon="pi pi-credit-card">
            <p-table [value]="cotisations" responsiveLayout="stack" styleClass="p-datatable-sm">
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
            <p-table [value]="abonnements" responsiveLayout="stack" styleClass="p-datatable-sm">
              <ng-template pTemplate="header">
                <tr>
                  <th>Saison</th>
                  <th>Bloc</th>
                  <th>Place</th>
                  <th>Montant Payé</th>
                  <th>Statut</th>
                </tr>
              </ng-template>
              <ng-template pTemplate="body" let-abo>
                <tr>
                  <td><span class="font-bold">{{ abo.seasonId }}</span></td>
                  <td>{{ abo.bloc || '-' }}</td>
                  <td>{{ abo.place || '-' }}</td>
                  <td>{{ abo.montantPaye | currency:'EUR':'symbol':'1.2-2':'fr' }}</td>
                  <td>
                    <p-tag [value]="abo.status"></p-tag>
                  </td>
                </tr>
              </ng-template>
              <ng-template pTemplate="emptymessage">
                <tr><td colspan="5" class="text-center p-3 text-500">Aucun abonnement enregistré.</td></tr>
              </ng-template>
            </p-table>
          </p-tabPanel>

          <!-- Travels Tab -->
          <p-tabPanel header="Déplacements" leftIcon="pi pi-car">
            <p-table [value]="travels" responsiveLayout="stack" styleClass="p-datatable-sm">
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
            <p-table [value]="benevolats" responsiveLayout="stack" styleClass="p-datatable-sm">
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
    </div>
  `
})
export class MemberDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private memberService = inject(MemberService);
  private cotisationService = inject(CotisationService);
  private abonnementService = inject(AbonnementService);
  private travelService = inject(TravelService);
  private benevolatService = inject(BenevolatService);

  member: Member | null = null;
  cotisations: PersonCotisation[] = [];
  abonnements: Abonnement[] = [];
  travels: PersonTravel[] = [];
  benevolats: Benevolat[] = [];

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (id) {
      this.loadMemberData(id);
    }
  }

  loadMemberData(id: number): void {
    this.memberService.getMemberById(id).subscribe({
      next: (data) => this.member = data
    });

    this.abonnementService.getAbonnements(undefined, id).subscribe({
      next: (page) => this.abonnements = page.content
    });

    this.travelService.getTravelsByPerson(id).subscribe({
      next: (data) => this.travels = data
    });

    this.benevolatService.getBenevolatsByPerson(id).subscribe({
      next: (data) => this.benevolats = data
    });
  }
}
