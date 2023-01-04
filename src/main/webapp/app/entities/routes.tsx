import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import PostComment from './post-comment';
import NewsArticle from './news-article';
import Bookmark from './bookmark';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route path="post-comment/*" element={<PostComment />} />
        <Route path="news-article/*" element={<NewsArticle />} />
        <Route path="bookmark/*" element={<Bookmark />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </div>
  );
};
