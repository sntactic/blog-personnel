import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Article, ArticleRequest } from '../../shared/models/Article';
import { environment } from '../../environments/Environment';

@Injectable({ providedIn: 'root' })
export class ArticleService {
  private http = inject(HttpClient);
  private baseUrl = `${environment.apiUrl}/articles`;

  getPublished(): Observable<Article[]> {
    return this.http.get<Article[]>(this.baseUrl);
  }

  getById(id: string): Observable<Article> {
    return this.http.get<Article>(`${this.baseUrl}/${id}`);
  }

  getByAuthor(authorId: string): Observable<Article[]> {
    return this.http.get<Article[]>(`${this.baseUrl}/author/${authorId}`);
  }

  getByTag(tag: string): Observable<Article[]> {
    return this.http.get<Article[]>(`${this.baseUrl}/tag/${tag}`);
  }

  create(request: ArticleRequest, newFiles: File[]): Observable<Article> {
    const formData = this.buildFormData(request, newFiles);
    return this.http.post<Article>(this.baseUrl, formData);
  }

  update(id: string, request: ArticleRequest, newFiles: File[]): Observable<Article> {
    const formData = this.buildFormData(request, newFiles);
    return this.http.put<Article>(`${this.baseUrl}/${id}`, formData);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  /**
   * Construit le multipart/form-data attendu par le backend :
   * - partie "article" : le JSON (title, content, tags, status, existingImages)
   * - partie "images" : les nouveaux fichiers sélectionnés (toujours "nouveaux" par nature)
   *
   * NE PAS fixer le header Content-Type manuellement : le navigateur le génère
   * automatiquement avec le bon "boundary" pour le multipart.
   */
  private buildFormData(request: ArticleRequest, newFiles: File[]): FormData {
    const formData = new FormData();

    formData.append('article', new Blob([JSON.stringify(request)], { type: 'application/json' }));

    newFiles.forEach((file) => formData.append('images', file));

    return formData;
  }
}
