import React, { useState, useEffect } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IBookmark } from 'app/shared/model/bookmark.model';
import { getEntities } from './bookmark.reducer';

export const Bookmark = () => {
  const dispatch = useAppDispatch();

  const location = useLocation();
  const navigate = useNavigate();

  const bookmarkList = useAppSelector(state => state.bookmark.entities);
  const loading = useAppSelector(state => state.bookmark.loading);

  useEffect(() => {
    dispatch(getEntities({}));
  }, []);

  const handleSyncList = () => {
    dispatch(getEntities({}));
  };

  return (
    <div>
      <h2 id="bookmark-heading" data-cy="BookmarkHeading">
        <Translate contentKey="tazeDailyApp.bookmark.home.title">Bookmarks</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="tazeDailyApp.bookmark.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/bookmark/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="tazeDailyApp.bookmark.home.createLabel">Create new Bookmark</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {bookmarkList && bookmarkList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th>
                  <Translate contentKey="tazeDailyApp.bookmark.id">ID</Translate>
                </th>
                <th>
                  <Translate contentKey="tazeDailyApp.bookmark.newsarticle">Newsarticle</Translate>
                </th>
                <th>
                  <Translate contentKey="tazeDailyApp.bookmark.user">User</Translate>
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {bookmarkList.map((bookmark, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/bookmark/${bookmark.id}`} color="link" size="sm">
                      {bookmark.id}
                    </Button>
                  </td>
                  <td>
                    {bookmark.newsarticle ? <Link to={`/news-article/${bookmark.newsarticle.id}`}>{bookmark.newsarticle.id}</Link> : ''}
                  </td>
                  <td>{bookmark.user ? bookmark.user.login : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/bookmark/${bookmark.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`/bookmark/${bookmark.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`/bookmark/${bookmark.id}/delete`} color="danger" size="sm" data-cy="entityDeleteButton">
                        <FontAwesomeIcon icon="trash" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.delete">Delete</Translate>
                        </span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="tazeDailyApp.bookmark.home.notFound">No Bookmarks found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default Bookmark;
