import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { LayoutComponent } from './layout/layout.component';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login.component').then(m => m.LoginComponent)
  },
  {
    path: '',
    component: LayoutComponent,
    canActivate: [authGuard],
    children: [
      {
        path: '',
        loadComponent: () => import('./features/dashboard/dashboard.component').then(m => m.DashboardComponent)
      },
      {
        path: 'members',
        loadComponent: () => import('./features/members/member-list.component').then(m => m.MemberListComponent)
      },
      {
        path: 'members/:id',
        loadComponent: () => import('./features/members/member-detail.component').then(m => m.MemberDetailComponent)
      },
      {
        path: 'seasons',
        loadComponent: () => import('./features/seasons/season-list.component').then(m => m.SeasonListComponent)
      },
      {
        path: 'teams',
        loadComponent: () => import('./features/teams/team-list.component').then(m => m.TeamListComponent)
      },
      {
        path: 'matches',
        loadComponent: () => import('./features/matches/match-list.component').then(m => m.MatchListComponent)
      },
      {
        path: 'abonnements',
        loadComponent: () => import('./features/abonnements/abonnement-list.component').then(m => m.AbonnementListComponent)
      },
      {
        path: 'cotisations',
        loadComponent: () => import('./features/cotisations/cotisation-list.component').then(m => m.CotisationListComponent)
      }
    ]
  },
  {
    path: '**',
    redirectTo: ''
  }
];
