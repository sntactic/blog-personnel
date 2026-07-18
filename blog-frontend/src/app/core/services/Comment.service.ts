import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/Environment';
import { CommentRequest } from '../../shared/models/Comment';
import { Comment } from '../../shared/models/Comment';

@Injectable({ providedIn: 'root' })
export class CommentService {
  private http = inject(HttpClient);
  private baseUrl = `${environment.apiUrl}/comments`;

  getByArticle(articleId: string): Observable<Comment[]> {
    return this.http.get<Comment[]>(`${this.baseUrl}/article/${articleId}`);
  }

  add(articleId: string, request: CommentRequest): Observable<Comment> {
    return this.http.post<Comment>(`${this.baseUrl}/article/${articleId}`, request);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
