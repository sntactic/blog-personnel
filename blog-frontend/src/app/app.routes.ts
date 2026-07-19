import { Routes } from '@angular/router';
import { authGuard } from './core/guards/Auth.guard';
import { roleGuard } from './core/guards/Role.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'articles', pathMatch: 'full' },

  {
    path: 'login',
    loadComponent: () => import('./features/auth/login/login').then((m) => m.LoginComponent),
  },
  {
    path: 'register',
    loadComponent: () => import('./features/auth/register/register').then((m) => m.RegisterComponent),
  },

  {
    path: 'articles',
    loadComponent: () =>
      import('./features/articles/article-list/article-list').then((m) => m.ArticleList),
  },
  {
    // Doit être déclarée AVANT 'articles/:id', sinon Angular interprète "new" comme un :id
    path: 'articles/new',
    loadComponent: () =>
      import('./features/articles/article-form/article-form').then((m) => m.ArticleForm),
    canActivate: [authGuard, roleGuard],
    data: { roles: ['AUTHOR', 'ADMIN'] },
  },
  {
    path: 'articles/:id/edit',
    loadComponent: () =>
      import('./features/articles/article-form/article-form').then((m) => m.ArticleForm),
    canActivate: [authGuard, roleGuard],
    data: { roles: ['AUTHOR', 'ADMIN'] },
  },
  {
    path: 'articles/:id',
    loadComponent: () =>
      import('./features/articles/article-detail/article-detail').then((m) => m.ArticleDetail),
  },

  {
    path: 'admin/dashboard',
    loadComponent: () => import('./features/admin/dashboard/dashboard').then((m) => m.Dashboard),
    canActivate: [authGuard, roleGuard],
    data: { roles: ['ADMIN'] },
  },
  {
    path: 'admin/users',
    loadComponent: () =>
      import('./features/admin/user-management/user-management').then((m) => m.UserManagement),
    canActivate: [authGuard, roleGuard],
    data: { roles: ['ADMIN'] },
  },

  {
    path: 'forbidden',
    loadComponent: () => import('./features/forbidden/forbidden').then((m) => m.Forbidden),
  },

  // Route "wildcard" : toute URL non reconnue affiche la page 404
  {
    path: '**',
    loadComponent: () => import('./features/not-found/not-found').then((m) => m.NotFound),
  },
];
