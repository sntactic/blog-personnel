import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/Environment';
import { Article, ArticleRequest } from '../../shared/models/Article';

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

  getMyArticles(): Observable<Article[]> {
    return this.http.get<Article[]>(`${this.baseUrl}/my-articles`);
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
  
  private buildFormData(request: ArticleRequest, newFiles: File[]): FormData {
    const formData = new FormData();

    formData.append('article', new Blob([JSON.stringify(request)], { type: 'application/json' }));

    newFiles.forEach((file) => formData.append('images', file));

    return formData;
  }
}
