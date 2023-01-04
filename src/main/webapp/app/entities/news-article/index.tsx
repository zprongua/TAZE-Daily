import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import NewsArticle from './news-article';
import NewsArticleDetail from './news-article-detail';
import NewsArticleUpdate from './news-article-update';
import NewsArticleDeleteDialog from './news-article-delete-dialog';

const NewsArticleRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<NewsArticle />} />
    <Route path="new" element={<NewsArticleUpdate />} />
    <Route path=":id">
      <Route index element={<NewsArticleDetail />} />
      <Route path="edit" element={<NewsArticleUpdate />} />
      <Route path="delete" element={<NewsArticleDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default NewsArticleRoutes;
