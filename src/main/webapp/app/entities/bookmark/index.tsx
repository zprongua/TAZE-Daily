import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Bookmark from './bookmark';
import BookmarkDetail from './bookmark-detail';
import BookmarkUpdate from './bookmark-update';
import BookmarkDeleteDialog from './bookmark-delete-dialog';

const BookmarkRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Bookmark />} />
    <Route path="new" element={<BookmarkUpdate />} />
    <Route path=":id">
      <Route index element={<BookmarkDetail />} />
      <Route path="edit" element={<BookmarkUpdate />} />
      <Route path="delete" element={<BookmarkDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default BookmarkRoutes;
