import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ArticleService } from '../../../core/services/Article.service';
import { ArticleStatus } from '../../../shared/models/Article';

@Component({
  selector: 'app-article-form',
  imports: [ReactiveFormsModule],
  templateUrl: './article-form.html',
  styleUrl: './article-form.scss',
})
export class ArticleForm {
  private readonly fb = inject(FormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly articleService = inject(ArticleService);

  protected articleId: string | null = null;
  protected readonly isEditMode = signal(false);
  protected readonly loading = signal(false);
  protected readonly saving = signal(false);
  protected readonly errorMessage = signal<string | null>(null);

  // Images déjà sur MinIO (mode édition) — le backend supprimera celles retirées d'ici
  protected readonly existingImages = signal<string[]>([]);
  // Nouveaux fichiers sélectionnés par l'utilisateur, pas encore uploadés,
  // avec leur URL de prévisualisation précalculée (évite de recréer l'URL à chaque rendu)
  protected readonly newFiles = signal<{ file: File; previewUrl: string }[]>([]);

  protected readonly form = this.fb.group({
    title: ['', Validators.required],
    content: ['', Validators.required],
    tags: ['', Validators.required], // saisi comme "tag1, tag2, tag3"
    status: ['DRAFT' as ArticleStatus, Validators.required],
  });

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.articleId = id;
      this.isEditMode.set(true);
      this.loadArticle(id);
    }
  }

  private loadArticle(id: string): void {
    this.loading.set(true);
    this.articleService.getById(id).subscribe({
      next: (article) => {
        this.form.patchValue({
          title: article.title,
          content: article.content,
          tags: article.tags.join(', '),
          status: article.status,
        });
        this.existingImages.set(article.images);
        this.loading.set(false);
      },
      error: () => {
        this.errorMessage.set('Impossible de charger cet article.');
        this.loading.set(false);
      },
    });
  }

  protected onFilesSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files) return;

    const added = Array.from(input.files).map((file) => ({
      file,
      previewUrl: URL.createObjectURL(file),
    }));
    this.newFiles.update((current) => [...current, ...added]);
    input.value = ''; // permet de resélectionner le même fichier si retiré puis rajouté
  }

  protected removeNewFile(entry: { file: File; previewUrl: string }): void {
    URL.revokeObjectURL(entry.previewUrl); // libère la mémoire allouée à la prévisualisation
    this.newFiles.update((current) => current.filter((f) => f !== entry));
  }

  protected removeExistingImage(url: string): void {
    this.existingImages.update((current) => current.filter((u) => u !== url));
  }

  protected submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.saving.set(true);
    this.errorMessage.set(null);

    const raw = this.form.getRawValue();
    const request = {
      title: raw.title!,
      content: raw.content!,
      tags: raw
        .tags!.split(',')
        .map((t) => t.trim())
        .filter((t) => t.length > 0),
      status: raw.status!,
      existingImages: this.existingImages(),
    };

    const rawFiles = this.newFiles().map((entry) => entry.file);

    const request$ =
      this.isEditMode() && this.articleId
        ? this.articleService.update(this.articleId, request, rawFiles)
        : this.articleService.create(request, rawFiles);

    request$.subscribe({
      next: (article) => {
        this.saving.set(false);
        this.router.navigate(['/articles', article.id]);
      },
      error: (err) => {
        this.saving.set(false);
        this.errorMessage.set(err.error?.message ?? "Impossible d'enregistrer l'article.");
      },
    });
  }
}
