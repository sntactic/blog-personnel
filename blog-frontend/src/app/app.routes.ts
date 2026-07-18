import { Routes } from '@angular/router';

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
    path: 'articles/:id',
    loadComponent: () =>
      import('./features/articles/article-detail/article-detail').then((m) => m.ArticleDetail),
  },

  { path: '**', redirectTo: 'articles' },
];
