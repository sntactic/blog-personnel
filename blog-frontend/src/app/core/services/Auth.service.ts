import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { AuthResponse, LoginRequest, RegisterRequest, Role } from '../../shared/models/User';
import { environment } from '../../environments/Environment';

const TOKEN_KEY = 'blog_token';

interface JwtPayload {
  sub: string; // email
  userId: string;
  role: Role;
  iat: number;
  exp: number;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private baseUrl = `${environment.apiUrl}/auth`;

  register(request: RegisterRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${this.baseUrl}/register`, request)
      .pipe(tap((res) => this.storeToken(res.token)));
  }

  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${this.baseUrl}/login`, request)
      .pipe(tap((res) => this.storeToken(res.token)));
  }

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
  }

  getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  isAuthenticated(): boolean {
    const payload = this.getPayload();
    if (!payload) return false;
    return payload.exp * 1000 > Date.now();
  }

  getRole(): Role | null {
    return this.getPayload()?.role ?? null;
  }

  getUserId(): string | null {
    return this.getPayload()?.userId ?? null;
  }

  hasAnyRole(roles: Role[]): boolean {
    const role = this.getRole();
    return role !== null && roles.includes(role);
  }

  private storeToken(token: string): void {
    localStorage.setItem(TOKEN_KEY, token);
  }

  // Décodage local du payload JWT (base64), UNIQUEMENT pour affichage UI.
  // Aucune vérification de signature ici : la sécurité réelle est appliquée côté backend.
  private getPayload(): JwtPayload | null {
    const token = this.getToken();
    if (!token) return null;
    try {
      const payloadBase64 = token.split('.')[1];
      const decoded = atob(payloadBase64.replace(/-/g, '+').replace(/_/g, '/'));
      return JSON.parse(decoded) as JwtPayload;
    } catch {
      return null;
    }
  }
}
