import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { MessageService } from 'primeng/api';
import { AuthService } from '../services/auth.service';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const messageService = inject(MessageService);
  const router = inject(Router);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      let errorMessage = 'Une erreur inattendue est survenue.';

      if (error.error) {
        if (typeof error.error === 'string') {
          errorMessage = error.error;
        } else if (error.error.detail) {
          errorMessage = error.error.detail;
        } else if (error.error.message) {
          errorMessage = error.error.message;
        }
      }

      if (error.status === 401) {
        messageService.add({
          severity: 'warn',
          summary: 'Session expirée',
          detail: 'Veuillez vous reconnecter.'
        });
        authService.logout();
      } else if (error.status === 403) {
        messageService.add({
          severity: 'error',
          summary: 'Accès refusé',
          detail: "Vous n'avez pas les droits nécessaires pour effectuer cette action."
        });
      } else if (error.status === 404) {
        messageService.add({
          severity: 'warn',
          summary: 'Non trouvé',
          detail: errorMessage
        });
      } else if (error.status >= 500) {
        messageService.add({
          severity: 'error',
          summary: 'Erreur Serveur',
          detail: errorMessage
        });
      } else if (error.status === 400) {
        messageService.add({
          severity: 'error',
          summary: 'Données invalides',
          detail: errorMessage
        });
      }

      return throwError(() => error);
    })
  );
};
