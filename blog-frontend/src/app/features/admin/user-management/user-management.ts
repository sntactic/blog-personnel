import { Component, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { UserService } from '../../../core/services/User.service';
import { AuthService } from '../../../core/services/Auth.service';
import { Role, User } from '../../../shared/models/User';

@Component({
  selector: 'app-user-management',
  imports: [DatePipe],
  templateUrl: './user-management.html',
  styleUrl: './user-management.scss',
})
export class UserManagement {
  private readonly userService = inject(UserService);
  private readonly authService = inject(AuthService);

  protected readonly roles: Role[] = ['ADMIN', 'AUTHOR', 'READER'];

  protected readonly users = signal<User[]>([]);
  protected readonly loading = signal(true);
  protected readonly error = signal<string | null>(null);
  protected readonly updatingUserId = signal<string | null>(null);

  ngOnInit(): void {
    this.loadUsers();
  }

  private loadUsers(): void {
    this.loading.set(true);
    this.userService.getAll().subscribe({
      next: (users) => {
        this.users.set(users);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossible de charger les utilisateurs.');
        this.loading.set(false);
      },
    });
  }

  protected isCurrentUser(user: User): boolean {
    return this.authService.getUserId() === user.id;
  }

  protected changeRole(user: User, newRole: Role): void {
    if (newRole === user.role) return;

    this.updatingUserId.set(user.id);
    this.userService.updateRole(user.id, newRole).subscribe({
      next: (updated) => {
        this.users.update((current) => current.map((u) => (u.id === updated.id ? updated : u)));
        this.updatingUserId.set(null);
      },
      error: () => {
        this.error.set('Impossible de modifier le rôle de cet utilisateur.');
        this.updatingUserId.set(null);
      },
    });
  }

  protected deleteUser(user: User): void {
    if (!confirm(`Supprimer définitivement le compte de ${user.fullName} ?`)) return;

    this.userService.delete(user.id).subscribe({
      next: () => this.users.update((current) => current.filter((u) => u.id !== user.id)),
      error: () => this.error.set('Impossible de supprimer cet utilisateur.'),
    });
  }
}
