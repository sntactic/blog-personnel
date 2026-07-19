import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/Auth.service';

/**
 * Gère les erreurs HTTP transverses à toute l'application :
 * - 401 : token invalide/expiré → déconnexion + redirection login
 * - 0 / erreur réseau : backend injoignable → message générique
 *
 * Les erreurs métier (400, 403, 404 avec un message applicatif) sont laissées
 * telles quelles : chaque composant les traite déjà via err.error?.message
 * (ex: formulaire de login qui affiche "email déjà utilisé").
 * Cet intercepteur ne fait que compléter, pas remplacer, cette gestion locale.
 */
export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401) {
        authService.logout();
        router.navigate(['/login']);
      } else if (error.status === 0) {
        // Le backend n'a pas répondu (arrêté, hors ligne, CORS bloqué...)
        console.error('Le serveur est injoignable. Vérifie que le backend est démarré.');
      }

      return throwError(() => error);
    }),
  );
};
