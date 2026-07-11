export interface Comment {
  id: string;
  articleId: string;
  authorName: string;
  content: string;
  createdAt: string;
}

export interface CommentRequest {
  authorName: string;
  content: string;
}
