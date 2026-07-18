import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login/login').then((c) => c.LoginComponent),
  },
  {
    path: 'register',
    loadComponent: () =>
      import('./features/auth/register/register').then((c) => c.RegisterComponent),
  },
  {
    path: 'articles',
    loadComponent: () =>
      import('./features/articles/article-list/article-list').then((m) => m.ArticleList),
  },

  { path: '**', redirectTo: 'articles' },
];
