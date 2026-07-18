import { Component, input } from '@angular/core';
import { DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Article } from '../../../shared/models/Article';

@Component({
  selector: 'app-article-card',
  imports: [RouterLink, DatePipe],
  templateUrl: './article-card.html',
  styleUrl: './article-card.scss',
})
export class ArticleCard {
  readonly article = input.required<Article>();
}
