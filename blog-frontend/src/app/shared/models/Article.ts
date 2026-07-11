export type ArticleStatus = 'DRAFT' | 'PUBLISHED';

export interface Article {
  id: string;
  title: string;
  content: string;
  authorId: string;
  tags: string[];
  images: string[];
  status: ArticleStatus;
  views: number;
  createdAt: string;
  updatedAt: string;
}

// Correspond au champ "article" (JSON) de la requête multipart
export interface ArticleRequest {
  title: string;
  content: string;
  tags: string[];
  status: ArticleStatus;
  existingImages?: string[]; // uniquement utilisé lors d'une modification
}
