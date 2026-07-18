import { Component, inject, input, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { CommentService } from '../../../core/services/Comment.service';
import { AuthService } from '../../../core/services/Auth.service';
import { Comment } from '../../../shared/models/Comment'

@Component({
  selector: 'app-comment-section',
  imports: [DatePipe, ReactiveFormsModule, RouterLink],
  templateUrl: './comment-section.html',
  styleUrl: './comment-section.scss',
})
export class CommentSection {
  readonly articleId = input.required<string>();

  private readonly commentService = inject(CommentService);
  protected readonly authService = inject(AuthService);
  private readonly fb = inject(FormBuilder);

  protected readonly comments = signal<Comment[]>([]);
  protected readonly loading = signal(true);
  protected readonly submitting = signal(false);
  protected readonly errorMessage = signal<string | null>(null);

  protected readonly form = this.fb.group({
    authorName: ['', Validators.required],
    content: ['', Validators.required],
  });

  ngOnInit(): void {
    this.loadComments();
  }

  private loadComments(): void {
    this.loading.set(true);
    this.commentService.getByArticle(this.articleId()).subscribe({
      next: (comments) => {
        this.comments.set(comments);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }

  protected submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.submitting.set(true);
    this.errorMessage.set(null);

    this.commentService
      .add(
        this.articleId(),
        this.form.getRawValue() as {
          authorName: string;
          content: string;
        },
      )
      .subscribe({
        next: (comment) => {
          this.comments.update((current) => [...current, comment]);
          this.form.reset();
          this.submitting.set(false);
        },
        error: (err) => {
          this.errorMessage.set(err.error?.message ?? "Impossible d'ajouter le commentaire.");
          this.submitting.set(false);
        },
      });
  }
}
