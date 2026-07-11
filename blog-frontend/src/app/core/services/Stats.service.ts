import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { MonthlyArticleCount, TagCount } from '../../shared/models/Stats';
import { environment } from '../../environments/Environment';

@Injectable({ providedIn: 'root' })
export class StatsService {
  private http = inject(HttpClient);
  private baseUrl = `${environment.apiUrl}/stats`;

  getArticlesPerMonth(): Observable<MonthlyArticleCount[]> {
    return this.http.get<MonthlyArticleCount[]>(`${this.baseUrl}/articles-per-month`);
  }

  getPopularTags(): Observable<TagCount[]> {
    return this.http.get<TagCount[]>(`${this.baseUrl}/popular-tags`);
  }
}
