import { Component, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { CommentSection } from '../../comments/comment-section/comment-section';
import { ArticleService } from '../../../core/services/Article.service';
import { AuthService } from '../../../core/services/Auth.service';
import { Article } from '../../../shared/models/Article';

@Component({
  selector: 'app-article-detail',
  imports: [DatePipe, RouterLink, CommentSection],
  templateUrl: './article-detail.html',
  styleUrl: './article-detail.scss',
})
export class ArticleDetail {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly articleService = inject(ArticleService);
  private readonly authService = inject(AuthService);

  protected readonly article = signal<Article | null>(null);
  protected readonly loading = signal(true);
  protected readonly error = signal<string | null>(null);
  protected readonly deleting = signal(false);

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) {
      this.error.set('Article introuvable.');
      this.loading.set(false);
      return;
    }
    this.loadArticle(id);
  }

  private loadArticle(id: string): void {
    this.loading.set(true);
    this.articleService.getById(id).subscribe({
      next: (article) => {
        this.article.set(article);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Cet article est introuvable ou a été supprimé.');
        this.loading.set(false);
      },
    });
  }

  protected canManage(): boolean {
    const article = this.article();
    if (!article || !this.authService.isAuthenticated()) return false;

    const isAdmin = this.authService.getRole() === 'ADMIN';
    const isOwner = this.authService.getUserId() === article.authorId;
    return isAdmin || isOwner;
  }

  protected deleteArticle(): void {
    const article = this.article();
    if (!article) return;

    if (!confirm('Supprimer définitivement cet article ?')) return;

    this.deleting.set(true);
    this.articleService.delete(article.id).subscribe({
      next: () => this.router.navigate(['/articles']),
      error: () => {
        this.deleting.set(false);
        this.error.set("Impossible de supprimer l'article.");
      },
    });
  }
}
