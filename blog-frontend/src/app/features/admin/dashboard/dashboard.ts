import { Component, computed, inject, signal } from '@angular/core';
import { forkJoin } from 'rxjs';
import { StatsService } from '../../../core/services/Stats.service';
import { MonthlyArticleCount, TagCount } from '../../../shared/models/Stats';

@Component({
  selector: 'app-dashboard',
  imports: [],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss',
})
export class Dashboard {
  private readonly statsService = inject(StatsService);

  protected readonly monthlyCounts = signal<MonthlyArticleCount[]>([]);
  protected readonly popularTags = signal<TagCount[]>([]);
  protected readonly loading = signal(true);
  protected readonly error = signal<string | null>(null);

  // Sert à calculer la largeur relative des barres (en %) par rapport à la valeur max
  protected readonly maxMonthlyCount = computed(() =>
    Math.max(1, ...this.monthlyCounts().map((m) => m.count)),
  );
  protected readonly maxTagCount = computed(() =>
    Math.max(1, ...this.popularTags().map((t) => t.count)),
  );

  ngOnInit(): void {
    this.loading.set(true);
    this.error.set(null);

    forkJoin({
      monthly: this.statsService.getArticlesPerMonth(),
      tags: this.statsService.getPopularTags(),
    }).subscribe({
      next: ({ monthly, tags }) => {
        this.monthlyCounts.set(monthly);
        this.popularTags.set(tags);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossible de charger les statistiques.');
        this.loading.set(false);
      },
    });
  }

  protected barWidth(count: number, max: number): number {
    return Math.round((count / max) * 100);
  }
}
