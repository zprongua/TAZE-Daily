import React, { useState, useEffect } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { INewsArticle } from 'app/shared/model/news-article.model';
import { getEntities } from './news-article.reducer';

export const NewsArticle = () => {
  const dispatch = useAppDispatch();

  const location = useLocation();
  const navigate = useNavigate();

  const newsArticleList = useAppSelector(state => state.newsArticle.entities);
  const loading = useAppSelector(state => state.newsArticle.loading);

  useEffect(() => {
    dispatch(getEntities({}));
  }, []);

  const handleSyncList = () => {
    dispatch(getEntities({}));
  };

  return (
    <div>
      <h2 id="news-article-heading" data-cy="NewsArticleHeading">
        <Translate contentKey="tazeDailyApp.newsArticle.home.title">News Articles</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="tazeDailyApp.newsArticle.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/news-article/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="tazeDailyApp.newsArticle.home.createLabel">Create new News Article</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {newsArticleList && newsArticleList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th>
                  <Translate contentKey="tazeDailyApp.newsArticle.id">ID</Translate>
                </th>
                <th>
                  <Translate contentKey="tazeDailyApp.newsArticle.title">Title</Translate>
                </th>
                <th>
                  <Translate contentKey="tazeDailyApp.newsArticle.author">Author</Translate>
                </th>
                <th>
                  <Translate contentKey="tazeDailyApp.newsArticle.article">Article</Translate>
                </th>
                <th>
                  <Translate contentKey="tazeDailyApp.newsArticle.genre">Genre</Translate>
                </th>
                <th>
                  <Translate contentKey="tazeDailyApp.newsArticle.date">Date</Translate>
                </th>
                <th>
                  <Translate contentKey="tazeDailyApp.newsArticle.likes">Likes</Translate>
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {newsArticleList.map((newsArticle, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/news-article/${newsArticle.id}`} color="link" size="sm">
                      {newsArticle.id}
                    </Button>
                  </td>
                  <td>{newsArticle.title}</td>
                  <td>{newsArticle.author}</td>
                  <td>{newsArticle.article}</td>
                  <td>
                    <Translate contentKey={`tazeDailyApp.Genre.${newsArticle.genre}`} />
                  </td>
                  <td>{newsArticle.date ? <TextFormat type="date" value={newsArticle.date} format={APP_LOCAL_DATE_FORMAT} /> : null}</td>
                  <td>{newsArticle.likes}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/news-article/${newsArticle.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`/news-article/${newsArticle.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/news-article/${newsArticle.id}/delete`}
                        color="danger"
                        size="sm"
                        data-cy="entityDeleteButton"
                      >
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
              <Translate contentKey="tazeDailyApp.newsArticle.home.notFound">No News Articles found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default NewsArticle;
