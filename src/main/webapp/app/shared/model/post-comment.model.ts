import dayjs from 'dayjs';
import { INewsArticle } from 'app/shared/model/news-article.model';
import { IUser } from 'app/shared/model/user.model';

export interface IPostComment {
  id?: number;
  body?: string | null;
  author?: string | null;
  timeStamp?: string | null;
  newsArticle?: INewsArticle | null;
  user?: IUser | null;
}

export const defaultValue: Readonly<IPostComment> = {};
