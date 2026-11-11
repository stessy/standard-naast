import { Component, OnInit, inject, signal, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { MessageService } from 'primeng/api';
import { finalize } from 'rxjs';
import { DesktopService, DesktopStorageSettings } from '../../core/services/desktop.service';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-desktop-settings',
  standalone: true,
  imports: [CommonModule, FormsModule, ButtonModule, InputTextModule],
  changeDetection: ChangeDetectionStrategy.Eager,
  template: `
    <section class="p-4">
      <h1>Paramètres de l'application</h1>
      @if (!auth.isAdmin()) {
        <p>Ces paramètres sont réservés aux administrateurs.</p>
      } @else if (!desktop.available()) {
        <p>Ces paramètres sont disponibles dans l'application desktop uniquement.</p>
      } @else {
        <p>La base reste sur cet ordinateur. Pour partager les données entre Mac et Windows,
          choisissez sur chaque ordinateur le même dossier synchronisé Google Drive.</p>
        <p class="font-bold">Une seule application à la fois. Attendez la fin de la synchronisation
          Google Drive après avoir quitté, et avant d'ouvrir l'application sur un autre ordinateur.</p>
        <div class="surface-card border-round p-4 flex flex-column gap-3">
          @if (choosingDirectory()) {
            <p role="status">Sélection du répertoire en cours dans la fenêtre Standard Naast.
              Sélectionnez un dossier ou cliquez sur Annuler pour revenir ici.</p>
          }
          <label for="backup-directory">Répertoire des sauvegardes SQLite</label>
          <div class="flex gap-2 flex-wrap">
            <input pInputText id="backup-directory" class="flex-1" [(ngModel)]="backupDirectory"
                   [disabled]="busy() || !loaded()" placeholder="Aucun : sauvegarde automatique désactivée" />
            <button pButton label="Parcourir..." icon="pi pi-folder-open"
                    [disabled]="busy() || !loaded()" (click)="choose('backup')"></button>
          </div>
          <small>Chaque arrêt propre crée une sauvegarde versionnée. Les anciennes versions sont conservées.
            Un répertoire vide désactive les sauvegardes partagées.
            Si le dossier choisi contient déjà des sauvegardes, la base locale sera conservée comme une version distincte ;
            le prochain démarrage demandera quelle version utiliser.</small>
          <label for="logs-directory">Répertoire des logs</label>
          <div class="flex gap-2 flex-wrap">
            <input pInputText id="logs-directory" class="flex-1" [(ngModel)]="logsDirectory"
                   [disabled]="busy() || !loaded()" />
            <button pButton label="Parcourir..." icon="pi pi-folder-open"
                    [disabled]="busy() || !loaded()" (click)="choose('logs')"></button>
          </div>
          <small>Le changement de répertoire des logs prend effet au prochain démarrage.
            Les fichiers existants ne sont pas déplacés.</small>
          @if (settings(); as current) {
            @if (current.backupError) {
              <p role="alert" class="text-red-700 font-bold">{{ current.backupError }}
                Corrigez le répertoire avant de quitter ou de changer d'ordinateur.</p>
            }
            @if (current.latestBackup) {
              <p>Dernière sauvegarde visible : {{ current.latestBackup | date:'dd/MM/yyyy HH:mm:ss' }}</p>
            }
            @if (current.conflictingVersions > 1) {
              <p class="text-orange-700 font-bold">Plusieurs versions concurrentes existent.
                Une sélection sera demandée au prochain démarrage.</p>
            }
            @if (current.logsRestartRequired) {
              <p>Redémarrage nécessaire pour utiliser le nouveau répertoire des logs.</p>
            }
          }
          <button pButton label="Enregistrer" icon="pi pi-save" class="align-self-start"
                  [loading]="busy()" [disabled]="busy() || !loaded()" (click)="save()"></button>
          @if (!loaded() && !busy()) {
            <button pButton label="Réessayer" [outlined]="true" class="align-self-start" (click)="load()"></button>
          }
        </div>
      }
    </section>
  `
})
export class DesktopSettingsComponent implements OnInit {
  readonly desktop = inject(DesktopService);
  readonly auth = inject(AuthService);
  private readonly messages = inject(MessageService);
  readonly settings = signal<DesktopStorageSettings | null>(null);
  readonly busy = signal(false);
  readonly loaded = signal(false);
  readonly choosingDirectory = signal(false);
  backupDirectory = '';
  logsDirectory = '';

  ngOnInit(): void {
    if (this.auth.isAdmin()) {
      this.desktop.loadRuntime().subscribe({
        next: runtime => { if (runtime.desktop) this.load(); },
        error: error => this.failed(error)
      });
    }
  }

  load(): void {
    this.busy.set(true);
    this.desktop.loadStorage().pipe(finalize(() => this.busy.set(false))).subscribe({
      next: settings => this.apply(settings),
      error: error => this.failed(error)
    });
  }

  choose(kind: 'backup' | 'logs'): void {
    if (this.busy()) return;
    this.busy.set(true);
    this.choosingDirectory.set(true);
    this.desktop.chooseDirectory(kind).pipe(finalize(() => {
      this.choosingDirectory.set(false);
      this.busy.set(false);
    })).subscribe({
      next: result => {
        if (result.directory === null) return;
        if (kind === 'backup') this.backupDirectory = result.directory;
        else this.logsDirectory = result.directory;
      },
      error: error => this.failed(error)
    });
  }

  save(): void {
    this.busy.set(true);
    this.desktop.saveStorage(this.backupDirectory, this.logsDirectory)
      .pipe(finalize(() => this.busy.set(false))).subscribe({
        next: settings => {
          this.apply(settings);
          this.messages.add({ severity: 'success', summary: 'Paramètres enregistrés',
            detail: 'Sauvegarde au prochain arrêt propre. Les nouveaux logs seront utilisés après redémarrage.' });
        },
        error: error => this.failed(error)
      });
  }

  private apply(settings: DesktopStorageSettings): void {
    this.settings.set(settings);
    this.backupDirectory = settings.backupDirectory;
    this.logsDirectory = settings.logsDirectory;
    this.loaded.set(true);
  }

  private failed(error: { error?: { detail?: string } }): void {
    this.messages.add({ severity: 'error', summary: 'Paramètres indisponibles',
      detail: error.error?.detail ?? 'Impossible de lire ou enregistrer les répertoires. Vérifiez leur disponibilité.' });
  }
}
