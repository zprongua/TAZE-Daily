import dayjs from 'dayjs';
import { IPostComment } from 'app/shared/model/post-comment.model';
import { Genre } from 'app/shared/model/enumerations/genre.model';

export interface INewsArticle {
  id?: number;
  title?: string | null;
  author?: string | null;
  article?: string | null;
  genre?: Genre | null;
  date?: string | null;
  likes?: number | null;
  postComments?: IPostComment[] | null;
}

export const defaultValue: Readonly<INewsArticle> = {};
