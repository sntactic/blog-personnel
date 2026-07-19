import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/Auth.service';
import { Role } from '../../shared/models/User';

/**
 * Usage dans app.routes.ts :
 * {
 *   path: 'admin',
 *   canActivate: [authGuard, roleGuard],
 *   data: { roles: ['ADMIN'] },
 *   ...
 * }
 */
export const roleGuard: CanActivateFn = (route) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const allowedRoles = route.data['roles'] as Role[] | undefined;

  if (!allowedRoles || authService.hasAnyRole(allowedRoles)) {
    return true;
  }

  router.navigate(['/forbidden']);
  return false;
};
