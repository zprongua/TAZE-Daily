import React, { useState, useEffect } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IPostComment } from 'app/shared/model/post-comment.model';
import { getEntities } from './post-comment.reducer';

export const PostComment = () => {
  const dispatch = useAppDispatch();

  const location = useLocation();
  const navigate = useNavigate();

  const postCommentList = useAppSelector(state => state.postComment.entities);
  const loading = useAppSelector(state => state.postComment.loading);

  useEffect(() => {
    dispatch(getEntities({}));
  }, []);

  const handleSyncList = () => {
    dispatch(getEntities({}));
  };

  return (
    <div>
      <h2 id="post-comment-heading" data-cy="PostCommentHeading">
        <Translate contentKey="tazeDailyApp.postComment.home.title">Post Comments</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="tazeDailyApp.postComment.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/post-comment/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="tazeDailyApp.postComment.home.createLabel">Create new Post Comment</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {postCommentList && postCommentList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th>
                  <Translate contentKey="tazeDailyApp.postComment.id">ID</Translate>
                </th>
                <th>
                  <Translate contentKey="tazeDailyApp.postComment.body">Body</Translate>
                </th>
                <th>
                  <Translate contentKey="tazeDailyApp.postComment.author">Author</Translate>
                </th>
                <th>
                  <Translate contentKey="tazeDailyApp.postComment.timeStamp">Time Stamp</Translate>
                </th>
                <th>
                  <Translate contentKey="tazeDailyApp.postComment.newsArticle">News Article</Translate>
                </th>
                <th>
                  <Translate contentKey="tazeDailyApp.postComment.user">User</Translate>
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {postCommentList.map((postComment, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/post-comment/${postComment.id}`} color="link" size="sm">
                      {postComment.id}
                    </Button>
                  </td>
                  <td>{postComment.body}</td>
                  <td>{postComment.author}</td>
                  <td>
                    {postComment.timeStamp ? <TextFormat type="date" value={postComment.timeStamp} format={APP_LOCAL_DATE_FORMAT} /> : null}
                  </td>
                  <td>
                    {postComment.newsArticle ? (
                      <Link to={`/news-article/${postComment.newsArticle.id}`}>{postComment.newsArticle.id}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td>{postComment.user ? postComment.user.login : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/post-comment/${postComment.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`/post-comment/${postComment.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/post-comment/${postComment.id}/delete`}
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
              <Translate contentKey="tazeDailyApp.postComment.home.notFound">No Post Comments found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default PostComment;
