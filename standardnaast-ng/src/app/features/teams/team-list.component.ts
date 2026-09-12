import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { DialogModule } from 'primeng/dialog';
import { ConfirmationService, MessageService } from 'primeng/api';
import { TeamService } from '../../core/services/team.service';
import { Team, TeamCreateUpdate } from '../../core/models/team.model';

@Component({
  selector: 'app-team-list',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    TableModule,
    ButtonModule,
    InputTextModule,
    DialogModule
  ],
  template: `
    <div class="teams-page flex flex-column gap-2">
      <div class="flex flex-column sm:flex-row justify-content-between align-items-start sm:align-items-center gap-2 bg-white px-3 py-2 border-round-xl border-1 border-200 shadow-1">
        <div>
          <h1 class="text-xl font-bold text-900 m-0">Gestion des Équipes</h1>
          <p class="text-500 text-xs m-0 mt-1">Liste des clubs et équipes adverses</p>
        </div>
        <button pButton label="Nouvelle Équipe" icon="pi pi-plus" class="p-button-danger p-button-sm font-bold" (click)="openNewDialog()"></button>
      </div>

      <div class="surface-card p-2 sm:p-3 border-round-xl border-1 border-200 shadow-1">
        <p-table [value]="teams" [loading]="loading" responsiveLayout="stack" styleClass="p-datatable-sm p-datatable-striped">
          <ng-template pTemplate="header">
            <tr>
              <th style="width: 15%">ID</th>
              <th style="width: 65%">Nom de l'équipe</th>
              <th style="width: 20%" class="text-center">Actions</th>
            </tr>
          </ng-template>
          <ng-template pTemplate="body" let-team>
            <tr>
              <td>{{ team.id }}</td>
              <td><span class="font-bold text-900">{{ team.name }}</span></td>
              <td class="text-center">
                <div class="flex justify-content-center gap-2">
                  <button pButton icon="pi pi-pencil" class="p-button-rounded p-button-text p-button-sm p-button-warning" (click)="openEditDialog(team)"></button>
                  <button pButton icon="pi pi-trash" class="p-button-rounded p-button-text p-button-sm p-button-danger" (click)="confirmDelete(team)"></button>
                </div>
              </td>
            </tr>
          </ng-template>
        </p-table>
      </div>

      <!-- Team Dialog -->
      <p-dialog [(visible)]="dialogVisible" [header]="isEditMode ? 'Modifier l\\'équipe' : 'Nouvelle équipe'" [modal]="true" [style]="{ width: '450px' }">
        <form [formGroup]="form" (ngSubmit)="saveTeam()" class="flex flex-column gap-3 mt-2">
          <div class="flex flex-column gap-2">
            <label for="name" class="font-semibold text-sm">Nom de l'équipe *</label>
            <input id="name" type="text" pInputText formControlName="name" placeholder="Ex: RSC Anderlecht" />
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
export class TeamListComponent implements OnInit {
  private teamService = inject(TeamService);
  private messageService = inject(MessageService);
  private confirmationService = inject(ConfirmationService);
  private fb = inject(FormBuilder);

  teams: Team[] = [];
  loading = false;
  dialogVisible = false;
  isEditMode = false;
  selectedTeamId: number | null = null;

  form: FormGroup = this.fb.group({
    name: ['', Validators.required]
  });

  ngOnInit(): void {
    this.loadTeams();
  }

  loadTeams(): void {
    this.loading = true;
    this.teamService.getAllTeams().subscribe({
      next: (data) => {
        this.teams = data;
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  openNewDialog(): void {
    this.isEditMode = false;
    this.selectedTeamId = null;
    this.form.reset();
    this.dialogVisible = true;
  }

  openEditDialog(team: Team): void {
    this.isEditMode = true;
    this.selectedTeamId = team.id;
    this.form.patchValue({ name: team.name });
    this.dialogVisible = true;
  }

  saveTeam(): void {
    if (this.form.invalid) return;
    const val = this.form.value as TeamCreateUpdate;

    if (this.isEditMode && this.selectedTeamId) {
      this.teamService.updateTeam(this.selectedTeamId, val).subscribe({
        next: () => {
          this.dialogVisible = false;
          this.messageService.add({ severity: 'success', summary: 'Succès', detail: 'Équipe modifiée' });
          this.loadTeams();
        }
      });
    } else {
      this.teamService.createTeam(val).subscribe({
        next: () => {
          this.dialogVisible = false;
          this.messageService.add({ severity: 'success', summary: 'Succès', detail: 'Équipe créée' });
          this.loadTeams();
        }
      });
    }
  }

  confirmDelete(team: Team): void {
    this.confirmationService.confirm({
      message: `Supprimer l'équipe ${team.name} ?`,
      acceptButtonStyleClass: 'p-button-danger',
      accept: () => {
        this.teamService.deleteTeam(team.id).subscribe({
          next: () => {
            this.messageService.add({ severity: 'success', summary: 'Supprimé', detail: 'Équipe supprimée' });
            this.loadTeams();
          }
        });
      }
    });
  }
}
