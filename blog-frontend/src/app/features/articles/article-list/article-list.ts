import { Component, inject, signal } from '@angular/core';
import { ArticleCard } from '../article-card/article-card';
import { ArticleService } from '../../../core/services/Article.service';
import { Article } from '../../../shared/models/Article';
@Component({
  selector: 'app-article-list',
  imports: [ArticleCard],
  templateUrl: './article-list.html',
  styleUrl: './article-list.scss',
})
export class ArticleList {
  private readonly articleService = inject(ArticleService);

  protected readonly articles = signal<Article[]>([]);
  protected readonly loading = signal(true);
  protected readonly error = signal<string | null>(null);

  constructor() {
    this.loadArticles();
  }

  private loadArticles(): void {
    this.loading.set(true);
    this.error.set(null);

    this.articleService.getPublished().subscribe({
      next: (articles) => {
        this.articles.set(articles);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossible de charger les articles pour le moment.');
        this.loading.set(false);
      },
    });
  }
}
