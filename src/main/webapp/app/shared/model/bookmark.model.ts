import { INewsArticle } from 'app/shared/model/news-article.model';
import { IUser } from 'app/shared/model/user.model';

export interface IBookmark {
  id?: number;
  newsarticle?: INewsArticle | null;
  user?: IUser | null;
}

export const defaultValue: Readonly<IBookmark> = {};
