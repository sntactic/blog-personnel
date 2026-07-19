import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ArticleCard } from '../article-card/article-card';
import { ArticleService } from '../../../core/services/Article.service';
import { Article } from '../../../shared/models/Article';

@Component({
  selector: 'app-article-list',
  imports: [ArticleCard, RouterLink],
  templateUrl: './article-list.html',
  styleUrl: './article-list.scss',
})
export class ArticleList {
  private readonly route = inject(ActivatedRoute);
  private readonly articleService = inject(ArticleService);

  protected readonly articles = signal<Article[]>([]);
  protected readonly loading = signal(true);
  protected readonly error = signal<string | null>(null);

  // Filtre actif, dérivé de l'URL (?tag=... ou ?author=...&authorName=...)
  protected readonly activeTag = signal<string | null>(null);
  protected readonly activeAuthorName = signal<string | null>(null);
  // true quand la route est /articles/mine (donnée statique de route, pas un query param)
  protected readonly myArticlesMode = signal(false);

  ngOnInit(): void {
    this.myArticlesMode.set(this.route.snapshot.data['mine'] === true);

    if (this.myArticlesMode()) {
      this.loadMyArticles();
      return;
    }

    this.route.queryParamMap.subscribe((params) => {
      const tag = params.get('tag');
      const authorId = params.get('author');
      const authorName = params.get('authorName');

      this.activeTag.set(tag);
      this.activeAuthorName.set(authorName);

      if (tag) {
        this.loadByTag(tag);
      } else if (authorId) {
        this.loadByAuthor(authorId);
      } else {
        this.loadPublished();
      }
    });
  }

  private loadPublished(): void {
    this.loading.set(true);
    this.error.set(null);
    this.articleService.getPublished().subscribe({
      next: (articles) => {
        this.articles.set(articles);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossible de charger les articles.');
        this.loading.set(false);
      },
    });
  }

  private loadByTag(tag: string): void {
    this.loading.set(true);
    this.error.set(null);
    this.articleService.getByTag(tag).subscribe({
      next: (articles) => {
        this.articles.set(articles);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossible de charger les articles pour ce tag.');
        this.loading.set(false);
      },
    });
  }

  private loadByAuthor(authorId: string): void {
    this.loading.set(true);
    this.error.set(null);
    this.articleService.getByAuthor(authorId).subscribe({
      next: (articles) => {
        this.articles.set(articles);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossible de charger les articles de cet auteur.');
        this.loading.set(false);
      },
    });
  }

  private loadMyArticles(): void {
    this.loading.set(true);
    this.error.set(null);
    this.articleService.getMyArticles().subscribe({
      next: (articles) => {
        this.articles.set(articles);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossible de charger tes articles.');
        this.loading.set(false);
      },
    });
  }
}
