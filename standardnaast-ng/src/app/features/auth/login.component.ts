import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { CardModule } from 'primeng/card';
import { InputTextModule } from 'primeng/inputtext';
import { PasswordModule } from 'primeng/password';
import { ButtonModule } from 'primeng/button';
import { MessageService } from 'primeng/api';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    CardModule,
    InputTextModule,
    PasswordModule,
    ButtonModule
  ],
  template: `
    <div class="login-container flex align-items-center justify-content-center min-h-screen bg-red-800 p-3">
      <div class="login-card surface-card p-5 shadow-4 border-round-xl w-full max-w-30rem">
        <!-- Logo & Header -->
        <div class="text-center mb-5">
          <i class="pi pi-shield text-5xl text-red-600 mb-3"></i>
          <h2 class="text-900 text-2xl font-bold mb-1">Standard de Naast</h2>
          <span class="text-600 font-medium text-sm">Gestion du Club des Supporters</span>
        </div>

        <!-- Login Form -->
        <form [formGroup]="loginForm" (ngSubmit)="onSubmit()" class="flex flex-column gap-3">
          <div class="flex flex-column gap-2">
            <label for="username" class="font-semibold text-sm text-700">Nom d'utilisateur</label>
            <span class="p-input-icon-left w-full">
              <i class="pi pi-user"></i>
              <input
                id="username"
                type="text"
                pInputText
                formControlName="username"
                class="w-full"
                placeholder="Ex: admin"
                [class.ng-invalid]="isFieldInvalid('username')"
                [class.ng-dirty]="isFieldInvalid('username')" />
            </span>
            @if (isFieldInvalid('username')) {
              <small class="text-red-600">Le nom d'utilisateur est requis.</small>
            }
          </div>

          <div class="flex flex-column gap-2">
            <label for="password" class="font-semibold text-sm text-700">Mot de passe</label>
            <p-password
              id="password"
              formControlName="password"
              [feedback]="false"
              [toggleMask]="true"
              styleClass="w-full"
              inputStyleClass="w-full"
              placeholder="••••••••">
            </p-password>
            @if (isFieldInvalid('password')) {
              <small class="text-red-600">Le mot de passe est requis.</small>
            }
          </div>

          <button
            pButton
            type="submit"
            label="Se connecter"
            icon="pi pi-sign-in"
            [loading]="loading"
            class="w-full mt-3 p-button-danger font-bold">
          </button>
        </form>
      </div>
    </div>
  `,
  styles: [`
    .login-container {
      background: linear-gradient(135deg, #991b1b 0%, #dc2626 50%, #7f1d1d 100%);
    }
  `]
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private messageService = inject(MessageService);

  loading = false;

  loginForm: FormGroup = this.fb.group({
    username: ['', Validators.required],
    password: ['', Validators.required]
  });

  isFieldInvalid(fieldName: string): boolean {
    const field = this.loginForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched));
  }

  onSubmit(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.authService.login(this.loginForm.value).subscribe({
      next: () => {
        this.loading = false;
        this.messageService.add({
          severity: 'success',
          summary: 'Connexion réussie',
          detail: 'Bienvenue sur Standard de Naast'
        });
        const returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
        this.router.navigateByUrl(returnUrl);
      },
      error: () => {
        this.loading = false;
        this.messageService.add({
          severity: 'error',
          summary: 'Erreur de connexion',
          detail: 'Identifiants invalides ou compte inactif.'
        });
      }
    });
  }
}
